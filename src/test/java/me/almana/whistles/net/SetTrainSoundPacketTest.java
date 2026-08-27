package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

class SetTrainSoundPacketTest {

	@Test
	void writesTheAutomaticArrivalSelection() {
		BlockPos pos = new BlockPos(8, 70, -12);
		ResourceLocation sound = ResourceLocation.fromNamespaceAndPath("whistles", "train_sound/test");
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

		try {
			new SetTrainSoundPacket(pos, sound, false).write(buffer);
			buffer.readBlockPos();
			buffer.readResourceLocation();

			assertEquals(1, buffer.readableBytes());
			assertFalse(buffer.readBoolean());
		} finally {
			buffer.release();
		}
	}

	@Test
	void readsTheAutomaticArrivalSelection() {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
		FriendlyByteBuf encoded = new FriendlyByteBuf(Unpooled.buffer());
		buffer.writeBlockPos(new BlockPos(8, 70, -12));
		buffer.writeResourceLocation(ResourceLocation.fromNamespaceAndPath("whistles", "train_sound/test"));
		buffer.writeBoolean(true);

		try {
			SetTrainSoundPacket packet = new SetTrainSoundPacket(buffer);
			packet.write(encoded);
			encoded.readBlockPos();
			encoded.readResourceLocation();

			assertEquals(0, buffer.readableBytes());
			assertTrue(encoded.readBoolean());
		} finally {
			buffer.release();
			encoded.release();
		}
	}
}
