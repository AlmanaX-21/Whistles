package me.almana.whistles.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.Config;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.PitchCodec;
import me.almana.whistles.sound.VolumeCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class TrainSounds {

	private static final float FADE_STEP = .34f;

	private static final Map<UUID, Channel[]> PLAYING = new HashMap<>();

	private static class Channel {
		boolean active;
		float semitones;
		float leverVolume = 100;
		float fade;
		ResourceLocation playingSound;
		TrainSoundInstance instance;
		TrainSoundSources.Source source;
	}

	public static void receive(TrainSoundPacket packet) {
		Channel channel = channelOf(packet.trainId, packet.mode);
		channel.active = packet.active;
		int range = Config.pitchRange();
		channel.semitones = PitchCodec.decode(packet.pitch, range);
		float pull = PitchCodec.normalizedPull(channel.semitones, range);
		channel.leverVolume = VolumeCodec.leverVolume(pull, Config.leverVolumeInfluence(), Config.leverVolumeMin(),
			Config.leverVolumeMax());
	}

	private static Channel channelOf(UUID trainId, SoundMode mode) {
		Channel[] channels = PLAYING.computeIfAbsent(trainId, id -> new Channel[SoundMode.values().length]);
		if (channels[mode.ordinal()] == null)
			channels[mode.ordinal()] = new Channel();
		return channels[mode.ordinal()];
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
			for (SoundMode mode : SoundMode.values()) {
				Channel channel = entry.getValue()[mode.ordinal()];
				if (channel == null)
					continue;
				anyAlive |= tickChannel(channel, train, mode);
			}
			if (!anyAlive)
				trains.remove();
		}
	}

	private static boolean tickChannel(Channel channel, Train train, SoundMode mode) {
		if (train == null) {
			reset(channel);
			return false;
		}

		if (channel.active && (channel.source == null || channel.source.stale()))
			channel.source = TrainSoundSources.find(train, mode, Minecraft.getInstance().level);

		if (channel.source == null) {
			reset(channel);
			return channel.active;
		}

		channel.fade = channel.active ? Math.min(1, channel.fade + FADE_STEP) : Math.max(0, channel.fade - FADE_STEP);

		if (channel.fade == 0) {
			reset(channel);
			return false;
		}

		if (channel.instance == null || channel.instance.isStopped()
			|| !channel.source.sound()
				.equals(channel.playingSound)) {
			stopInstance(channel);
			channel.playingSound = channel.source.sound();
			channel.instance = new TrainSoundInstance(channel.playingSound, Config.hearingRange());
			Minecraft.getInstance()
				.getSoundManager()
				.play(channel.instance);
		}

		channel.instance.setVolume(channel.fade * Config.volume() * (channel.leverVolume / 100f));
		channel.instance.setPitch(PitchCodec.playbackPitch(channel.semitones));
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
		channel.fade = 0;
	}

	public static void clear() {
		PLAYING.values()
			.forEach(channels -> {
				for (Channel channel : channels)
					if (channel != null)
						reset(channel);
			});
		PLAYING.clear();
	}
}
