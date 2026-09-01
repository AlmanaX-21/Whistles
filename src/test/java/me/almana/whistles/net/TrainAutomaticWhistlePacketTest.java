package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import me.almana.whistles.sound.TrainSoundSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

class TrainAutomaticWhistlePacketTest {

	private static final UUID TRAIN_ID = UUID.fromString("d6d9a2c1-6467-4ae9-a070-667859e1543c");
	private static final BlockPos LOCAL_POS = new BlockPos(-4, 2, 9);
	private static final ResourceLocation SOUND = new ResourceLocation("railway_pack", "train_sound/deep_horn");
	private static final TrainSoundSettings SETTINGS = new TrainSoundSettings(5, .35f, 104, 1.8f, 20, 95);

	@Test
	void roundTripsTheSelectedMovingWhistle() {
		TrainAutomaticWhistlePacket decoded = roundTrip(new TrainAutomaticWhistlePacket(
			TRAIN_ID, 73, LOCAL_POS, SOUND, true, SETTINGS));

		assertEquals(TRAIN_ID, decoded.trainId);
		assertEquals(73, decoded.entityId);
		assertEquals(LOCAL_POS, decoded.localPos);
		assertEquals(SOUND, decoded.sound);
		assertTrue(decoded.honking);
		assertEquals(SETTINGS, decoded.settings);
	}

	@Test
	void roundTripsTheReleaseState() {
		TrainAutomaticWhistlePacket decoded = roundTrip(new TrainAutomaticWhistlePacket(
			TRAIN_ID, 73, LOCAL_POS, SOUND, false, SETTINGS));

		assertFalse(decoded.honking);
	}

	private static TrainAutomaticWhistlePacket roundTrip(TrainAutomaticWhistlePacket packet) {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
		try {
			packet.write(buffer);
			return new TrainAutomaticWhistlePacket(buffer);
		} finally {
			buffer.release();
		}
	}
}
