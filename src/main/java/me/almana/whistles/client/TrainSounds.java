package me.almana.whistles.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.net.TrainArrivalSoundPacket;
import me.almana.whistles.net.TrainAutomaticWhistlePacket;
import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.PitchCodec;
import me.almana.whistles.sound.TrainSoundSettings;
import me.almana.whistles.sound.VolumeCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TrainSounds {

	private static final float FADE_STEP = .34f;

	private static final Map<UUID, Channel[]> PLAYING = new HashMap<>();
	private static final Map<UUID, AutomaticChannel> AUTOMATIC = new HashMap<>();

	private static class Channel {
		boolean active;
		byte pitch;
		float fade;
		TrainSoundSettings settings;
		ResourceLocation playingSound;
		TrainSoundInstance instance;
		TrainSoundSources.Source source;
	}

	private static class AutomaticChannel {
		int entityId;
		int honkTicks;
		BlockPos localPos;
		ResourceLocation sound;
		TrainSoundSettings settings;
		ResourceLocation playingSound;
		int playingRange;
		TrainSoundInstance instance;
	}

	public static void receive(TrainSoundPacket packet) {
		if (!TrainSoundPacket.isValidSourceIndex(packet.sourceIndex))
			return;
		Channel channel = channelOf(packet.trainId, packet.sourceIndex);
		channel.active = packet.active;
		channel.pitch = packet.pitch;
		channel.settings = packet.settings;
	}

	public static void playArrival(TrainArrivalSoundPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || !(mc.level.getEntity(packet.entityId) instanceof CarriageContraptionEntity entity))
			return;
		Vec3 location = entity.toGlobalVector(Vec3.atCenterOf(packet.localPos), 1);
		mc.getSoundManager()
			.play(new TrainArrivalSoundInstance(packet.sound, packet.settings, location));
	}

	public static void receiveAutomatic(TrainAutomaticWhistlePacket packet) {
		AutomaticChannel channel = AUTOMATIC.computeIfAbsent(packet.trainId, id -> new AutomaticChannel());
		channel.entityId = packet.entityId;
		channel.localPos = packet.localPos;
		channel.sound = packet.sound;
		channel.settings = packet.settings;
		channel.honkTicks = receiveAutomaticHonk(channel.honkTicks, packet.honking);
	}

	private static Channel channelOf(UUID trainId, int sourceIndex) {
		Channel[] channels = PLAYING.computeIfAbsent(trainId, id -> new Channel[TrainSoundPacket.MAX_SOURCES]);
		if (channels[sourceIndex] == null)
			channels[sourceIndex] = new Channel();
		return channels[sourceIndex];
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			clear();
			return;
		}

		Iterator<Map.Entry<UUID, Channel[]>> trains = PLAYING.entrySet()
			.iterator();
		while (trains.hasNext()) {
			Map.Entry<UUID, Channel[]> entry = trains.next();
			Train train = Create.RAILWAYS.sided(null).trains.get(entry.getKey());
			boolean anyAlive = false;
			for (int sourceIndex = 0; sourceIndex < entry.getValue().length; sourceIndex++) {
				Channel channel = entry.getValue()[sourceIndex];
				if (channel == null)
					continue;
				anyAlive |= tickChannel(channel, train, sourceIndex);
			}
			if (!anyAlive)
				trains.remove();
		}
		tickAutomatic(mc);
	}

	private static void tickAutomatic(Minecraft mc) {
		Iterator<AutomaticChannel> channels = AUTOMATIC.values()
			.iterator();
		while (channels.hasNext()) {
			AutomaticChannel channel = channels.next();
			channel.honkTicks = tickAutomaticHonk(channel.honkTicks);
			if (channel.honkTicks == 0) {
				stopAutomatic(channel);
				channels.remove();
				continue;
			}

			if (!(mc.level.getEntity(channel.entityId) instanceof CarriageContraptionEntity entity)) {
				stopAutomatic(channel);
				continue;
			}

			boolean soundChanged = channel.instance != null && automaticSoundChanged(channel.playingSound,
				channel.playingRange, channel.sound, channel.settings.hearingRange());
			if (channel.instance == null || channel.instance.isStopped() || soundChanged) {
				stopAutomatic(channel);
				channel.playingSound = channel.sound;
				channel.playingRange = channel.settings.hearingRange();
				channel.instance = new TrainSoundInstance(channel.playingSound, channel.playingRange);
				mc.getSoundManager()
					.play(channel.instance);
			}
			channel.instance.setVolume(automaticHonkVolume(channel.honkTicks) * channel.settings.volume());
			channel.instance.setPitch(1);
			channel.instance.setLocation(entity.toGlobalVector(Vec3.atCenterOf(channel.localPos), 1));
		}
	}

	static int receiveAutomaticHonk(int honkTicks, boolean honking) {
		if (honking)
			return honkTicks == 0 ? 20 : 13;
		return honkTicks > 5 ? 6 : 0;
	}

	static int tickAutomaticHonk(int honkTicks) {
		return Math.max(0, honkTicks - 1);
	}

	static float automaticHonkVolume(int honkTicks) {
		float fadeout = Mth.clamp((3 - honkTicks) / 3f, 0, 1);
		float fadein = Mth.clamp((honkTicks - 17) / 3f, 0, 1);
		return 1 - fadeout - fadein;
	}

	static boolean automaticSoundChanged(ResourceLocation playingSound, int playingRange, ResourceLocation sound,
		int range) {
		return !sound.equals(playingSound) || range != playingRange;
	}

	private static boolean tickChannel(Channel channel, Train train, int sourceIndex) {
		if (train == null) {
			reset(channel);
			return false;
		}

		if (channel.active && (channel.source == null || channel.source.stale())) {
			stopInstance(channel);
			var sources = TrainSoundSources.find(train, Minecraft.getInstance().level);
			channel.source = sourceIndex < sources.size() ? sources.get(sourceIndex) : null;
		}

		if (channel.source == null) {
			reset(channel);
			return channel.active;
		}

		channel.fade = channel.active ? Math.min(1, channel.fade + FADE_STEP) : Math.max(0, channel.fade - FADE_STEP);

		if (channel.fade == 0) {
			reset(channel);
			return false;
		}

		TrainSoundSettings settings = channel.settings;
		float semitones = PitchCodec.decode(channel.pitch, settings.pitchRange());
		float pull = PitchCodec.normalizedPull(semitones, settings.pitchRange());
		float leverVolume = VolumeCodec.leverVolume(pull, settings.leverVolumeInfluence(), settings.leverVolumeMin(),
			settings.leverVolumeMax());

		if (channel.instance == null || channel.instance.isStopped()
			|| !channel.source.sound()
				.equals(channel.playingSound)) {
			stopInstance(channel);
			channel.playingSound = channel.source.sound();
			channel.instance = new TrainSoundInstance(channel.playingSound, settings.hearingRange());
			Minecraft.getInstance()
				.getSoundManager()
				.play(channel.instance);
		}

		channel.instance.setVolume(channel.fade * settings.volume() * (leverVolume / 100f));
		channel.instance.setPitch(PitchCodec.playbackPitch(semitones));
		channel.instance.setLocation(channel.source.worldPosition());
		return true;
	}

	/** Tears down the audio only. The source stays put so a sound swap keeps its fade. */
	private static void stopInstance(Channel channel) {
		if (channel.instance != null) {
			channel.instance.stopSound();
			channel.instance = null;
		}
		channel.playingSound = null;
	}

	private static void reset(Channel channel) {
		stopInstance(channel);
		channel.source = null;
		if (!channel.active)
			channel.settings = null;
		channel.fade = 0;
	}

	private static void stopAutomatic(AutomaticChannel channel) {
		if (channel.instance != null) {
			channel.instance.stopSound();
			channel.instance = null;
		}
		channel.playingSound = null;
		channel.playingRange = 0;
	}

	public static void clear() {
		PLAYING.values()
			.forEach(channels -> {
				for (Channel channel : channels)
					if (channel != null)
						reset(channel);
			});
		PLAYING.clear();
		AUTOMATIC.values()
			.forEach(TrainSounds::stopAutomatic);
		AUTOMATIC.clear();
	}
}
