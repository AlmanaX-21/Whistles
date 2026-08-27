package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import me.almana.whistles.sound.TrainSoundSettings;
import net.minecraft.network.FriendlyByteBuf;

class TrainSoundPacketTest {

	@Test
	void roundTripsIndependentSourceIndex() {
		UUID trainId = UUID.fromString("d6d9a2c1-6467-4ae9-a070-667859e1543c");
		TrainSoundSettings settings = new TrainSoundSettings(8, .55f, 96, 1.6f, 10, 90);
		TrainSoundPacket packet = new TrainSoundPacket(trainId, 2, true, (byte) -37, settings);
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

		try {
			packet.write(buffer);
			TrainSoundPacket decoded = new TrainSoundPacket(buffer);

			assertEquals(trainId, decoded.trainId);
			assertEquals(2, decoded.sourceIndex);
			assertTrue(decoded.active);
			assertEquals((byte) -37, decoded.pitch);
			assertEquals(settings, decoded.settings);
		} finally {
			buffer.release();
		}
	}

	@Test
	void acceptsOnlyTheFirstThreeSourceIndices() {
		assertTrue(TrainSoundPacket.isValidSourceIndex(0));
		assertTrue(TrainSoundPacket.isValidSourceIndex(2));
		assertFalse(TrainSoundPacket.isValidSourceIndex(-1));
		assertFalse(TrainSoundPacket.isValidSourceIndex(3));
	}
}
