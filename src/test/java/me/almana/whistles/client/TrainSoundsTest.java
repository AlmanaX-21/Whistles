package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.resources.ResourceLocation;

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

	@Test
	void automaticWhistleRefreshExpiresWithoutAReleasePacket() {
		int ticks = TrainSounds.receiveAutomaticHonk(0, true);
		assertEquals(20, ticks);

		for (int i = 0; i < 20; i++)
			ticks = TrainSounds.tickAutomaticHonk(ticks);

		assertEquals(0, ticks);
	}

	@Test
	void automaticWhistleRefreshAndReleaseMatchCreate() {
		assertEquals(13, TrainSounds.receiveAutomaticHonk(20, true));
		assertEquals(6, TrainSounds.receiveAutomaticHonk(13, false));
		assertEquals(0, TrainSounds.receiveAutomaticHonk(5, false));
	}

	@Test
	void automaticWhistleFadesAtBothEnds() {
		assertEquals(1 / 3f, TrainSounds.automaticHonkVolume(19), .0001f);
		assertEquals(1, TrainSounds.automaticHonkVolume(17), .0001f);
		assertEquals(1, TrainSounds.automaticHonkVolume(3), .0001f);
		assertEquals(2 / 3f, TrainSounds.automaticHonkVolume(2), .0001f);
		assertEquals(0, TrainSounds.automaticHonkVolume(0), .0001f);
	}

	@Test
	void automaticWhistleRestartsForAuthoritativeSoundChanges() {
		ResourceLocation first = new ResourceLocation("whistles", "train_sound/first");
		ResourceLocation second = new ResourceLocation("whistles", "train_sound/second");

		assertFalse(TrainSounds.automaticSoundChanged(first, 64, first, 64));
		assertTrue(TrainSounds.automaticSoundChanged(first, 64, second, 64));
		assertTrue(TrainSounds.automaticSoundChanged(first, 64, first, 96));
	}
}
