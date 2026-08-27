package me.almana.whistles.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import me.almana.whistles.Whistles;

class PayloadTypesTest {

	@Test
	void keepsEveryPayloadOnItsStableIdentifier() {
		assertEquals(Whistles.asResource("set_train_sound"), SetTrainSoundPacket.TYPE.id());
		assertEquals(Whistles.asResource("train_sound"), TrainSoundPacket.TYPE.id());
		assertEquals(Whistles.asResource("train_arrival_sound"), TrainArrivalSoundPacket.TYPE.id());
		assertEquals(Whistles.asResource("set_train_sound_settings"), SetTrainSoundSettingsPacket.TYPE.id());
	}
}
