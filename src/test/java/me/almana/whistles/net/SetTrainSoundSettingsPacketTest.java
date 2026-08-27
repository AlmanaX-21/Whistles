package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import me.almana.whistles.sound.TrainSoundSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

class SetTrainSoundSettingsPacketTest {

	@Test
	void roundTripsTheTargetPostAndSettings() {
		BlockPos pos = new BlockPos(-18, 72, 35);
		TrainSoundSettings settings = new TrainSoundSettings(9, .4f, 112, 2.25f, 8, 93);
		SetTrainSoundSettingsPacket packet = new SetTrainSoundSettingsPacket(pos, settings);
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

		try {
			packet.write(buffer);
			SetTrainSoundSettingsPacket decoded = new SetTrainSoundSettingsPacket(buffer);

			assertEquals(pos, decoded.pos);
			assertEquals(settings, decoded.settings);
		} finally {
			buffer.release();
		}
	}
}
