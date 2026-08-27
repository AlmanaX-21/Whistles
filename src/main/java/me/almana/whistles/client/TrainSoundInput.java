package me.almana.whistles.client;

import java.util.UUID;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.PitchCodec;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.network.PacketDistributor;

public class TrainSoundInput {

	private static final int PACKET_RATE = 5;

	private static final boolean[] SOUNDING = new boolean[TrainSoundPacket.MAX_SOURCES];
	private static final byte[] SENT_PITCH = new byte[TrainSoundPacket.MAX_SOURCES];
	private static final TrainSoundSettings[] SENT_SETTINGS = new TrainSoundSettings[TrainSoundPacket.MAX_SOURCES];
	private static final int[] COOLDOWN = new int[TrainSoundPacket.MAX_SOURCES];

	private static UUID lastTrainId;

	public static void tick() {
		Screen screen = Minecraft.getInstance().screen;
		boolean driving = ControlsHandler.getContraption() instanceof CarriageContraptionEntity
			&& (screen == null || screen instanceof WhistleControlScreen);
		if (!driving)
			releaseAll();
	}

	public static void sendIfChanged(int sourceIndex, UUID trainId, boolean held, float semitones,
		TrainSoundSettings settings) {
		byte pitch = PitchCodec.encode(semitones, settings.pitchRange());

		if (COOLDOWN[sourceIndex] > 0)
			COOLDOWN[sourceIndex]--;

		boolean changed = held != SOUNDING[sourceIndex] || pitch != SENT_PITCH[sourceIndex];
		if (!held && !SOUNDING[sourceIndex])
			return;
		if (!changed && COOLDOWN[sourceIndex] > 0)
			return;

		SOUNDING[sourceIndex] = held;
		SENT_PITCH[sourceIndex] = pitch;
		SENT_SETTINGS[sourceIndex] = settings;
		COOLDOWN[sourceIndex] = PACKET_RATE;
		lastTrainId = trainId;
		PacketDistributor.sendToServer(new TrainSoundPacket(trainId, sourceIndex, held, pitch, settings));
	}

	public static void releaseAll() {
		for (int sourceIndex = 0; sourceIndex < SOUNDING.length; sourceIndex++) {
			if (!SOUNDING[sourceIndex])
				continue;
			SOUNDING[sourceIndex] = false;
			if (lastTrainId != null)
				PacketDistributor.sendToServer(
					new TrainSoundPacket(lastTrainId, sourceIndex, false, SENT_PITCH[sourceIndex],
						SENT_SETTINGS[sourceIndex]));
		}
	}
}
