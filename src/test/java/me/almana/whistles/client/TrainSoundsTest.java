package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.TrainSoundSettings;

class TrainSoundsTest {

	@AfterEach
	void clearChannels() {
		TrainSounds.clear();
	}

	@Test
	void keepsAuthoritativeSettingsWhileActiveChannelWaitsForSource() throws Exception {
		UUID trainId = UUID.randomUUID();
		TrainSoundSettings settings = new TrainSoundSettings(7, .65f, 72, 1.4f, 12, 88);
		TrainSounds.receive(new TrainSoundPacket(trainId, 0, true, (byte) 25, settings));

		Field playingField = TrainSounds.class.getDeclaredField("PLAYING");
		playingField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<UUID, Object[]> playing = (Map<UUID, Object[]>) playingField.get(null);
		Object channel = playing.get(trainId)[0];

		Method reset = TrainSounds.class.getDeclaredMethod("reset", channel.getClass());
		reset.setAccessible(true);
		reset.invoke(null, channel);

		Field settingsField = channel.getClass()
			.getDeclaredField("settings");
		settingsField.setAccessible(true);
		assertEquals(settings, settingsField.get(channel));
	}
}
