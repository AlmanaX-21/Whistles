package me.almana.whistles.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

class TrainSoundSettingsTest {

	private static final TrainSoundSettings SETTINGS = new TrainSoundSettings(7, .65f, 91, 1.75f, 12, 88);

	@Test
	void roundTripsBlockEntityData() {
		CompoundTag tag = SETTINGS.write();

		assertEquals(SETTINGS, TrainSoundSettings.read(tag));
	}

	@Test
	void roundTripsPacketData() {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
		try {
			SETTINGS.write(buffer);

			assertEquals(SETTINGS, TrainSoundSettings.read(buffer));
		} finally {
			buffer.release();
		}
	}

	@Test
	void validatesEveryServerControlledRange() {
		assertTrue(SETTINGS.valid());

		List<TrainSoundSettings> invalid = List.of(
			new TrainSoundSettings(0, .65f, 91, 1.75f, 12, 88),
			new TrainSoundSettings(13, .65f, 91, 1.75f, 12, 88),
			new TrainSoundSettings(7, -.01f, 91, 1.75f, 12, 88),
			new TrainSoundSettings(7, Float.NaN, 91, 1.75f, 12, 88),
			new TrainSoundSettings(7, .65f, 7, 1.75f, 12, 88),
			new TrainSoundSettings(7, .65f, 129, 1.75f, 12, 88),
			new TrainSoundSettings(7, .65f, 91, .09f, 12, 88),
			new TrainSoundSettings(7, .65f, 91, Float.POSITIVE_INFINITY, 12, 88),
			new TrainSoundSettings(7, .65f, 91, 1.75f, -1, 88),
			new TrainSoundSettings(7, .65f, 91, 1.75f, 12, 101));

		invalid.forEach(settings -> assertFalse(settings.valid(), settings.toString()));
	}
}
