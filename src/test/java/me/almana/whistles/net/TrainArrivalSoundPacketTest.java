package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import me.almana.whistles.sound.TrainSoundSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

class TrainArrivalSoundPacketTest {

	@Test
	void roundTripsTheSelectedMovingSoundSource() {
		BlockPos localPos = new BlockPos(-4, 2, 9);
		ResourceLocation sound = new ResourceLocation("railway_pack", "train_sound/deep_horn");
		TrainSoundSettings settings = new TrainSoundSettings(5, .35f, 104, 1.8f, 20, 95);
		TrainArrivalSoundPacket packet = new TrainArrivalSoundPacket(73, localPos, sound, settings);
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

		try {
			packet.write(buffer);
			TrainArrivalSoundPacket decoded = new TrainArrivalSoundPacket(buffer);

			assertEquals(73, decoded.entityId);
			assertEquals(localPos, decoded.localPos);
			assertEquals(sound, decoded.sound);
			assertEquals(settings, decoded.settings);
		} finally {
			buffer.release();
		}
	}
}
