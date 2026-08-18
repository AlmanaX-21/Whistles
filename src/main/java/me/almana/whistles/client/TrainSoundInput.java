package me.almana.whistles.client;

import java.util.UUID;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import me.almana.whistles.AllPackets;
import me.almana.whistles.Config;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.PitchCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class TrainSoundInput {

	private static final int PACKET_RATE = 5;

	private static final float[] SEMITONES = new float[SoundMode.values().length];
	private static final boolean[] SOUNDING = new boolean[SoundMode.values().length];
	private static final byte[] SENT_PITCH = new byte[SoundMode.values().length];
	private static final int[] COOLDOWN = new int[SoundMode.values().length];

	private static UUID lastTrainId;

	public static void tick() {
		Screen screen = Minecraft.getInstance().screen;
		boolean driving = ControlsHandler.getContraption() instanceof CarriageContraptionEntity
			&& (screen == null || screen instanceof WhistleControlScreen);
		if (!driving) {
			releaseAll();
			return;
		}
		if (screen instanceof WhistleControlScreen)
			return; // GUI owns SOUNDING/COOLDOWN state while open, see WhistleControlScreen.tick()

		lastTrainId = ((CarriageContraptionEntity) ControlsHandler.getContraption()).trainId;
		for (SoundMode mode : SoundMode.values())
			tickMode(mode, lastTrainId);
	}

	private static void tickMode(SoundMode mode, UUID trainId) {
		int i = mode.ordinal();
		int range = Config.pitchRange();
		float step = (float) range / Config.sweepTicks();

		if (AllKeys.up(mode)
			.isDown())
			SEMITONES[i] = PitchCodec.clampSemitones(SEMITONES[i] + step, range);
		if (AllKeys.down(mode)
			.isDown())
			SEMITONES[i] = PitchCodec.clampSemitones(SEMITONES[i] - step, range);

		boolean held = AllKeys.sound(mode)
			.isDown();
		sendIfChanged(mode, trainId, held, SEMITONES[i]);
	}

	public static void sendIfChanged(SoundMode mode, UUID trainId, boolean held, float semitones) {
		int i = mode.ordinal();
		int range = Config.pitchRange();
		byte pitch = PitchCodec.encode(semitones, range);

		if (COOLDOWN[i] > 0)
			COOLDOWN[i]--;

		boolean changed = held != SOUNDING[i] || pitch != SENT_PITCH[i];
		if (!held && !SOUNDING[i])
			return;
		if (!changed && COOLDOWN[i] > 0)
			return;

		SOUNDING[i] = held;
		SENT_PITCH[i] = pitch;
		COOLDOWN[i] = PACKET_RATE;
		lastTrainId = trainId;
		AllPackets.CHANNEL.sendToServer(new TrainSoundPacket(trainId, mode, held, pitch));
	}

	public static void releaseAll() {
		for (SoundMode mode : SoundMode.values()) {
			int i = mode.ordinal();
			if (!SOUNDING[i])
				continue;
			SOUNDING[i] = false;
			if (lastTrainId != null)
				AllPackets.CHANNEL.sendToServer(new TrainSoundPacket(lastTrainId, mode, false, SENT_PITCH[i]));
		}
	}
}
