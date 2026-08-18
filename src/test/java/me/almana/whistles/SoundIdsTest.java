package me.almana.whistles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import me.almana.whistles.sound.SoundIds;

class SoundIdsTest {

	@Test
	void acceptsOnlyPrefixedPathsWithAName() {
		assertTrue(SoundIds.isTrainSound("train_sound/steam_whistle"));
		assertFalse(SoundIds.isTrainSound("train_sound/"));
		assertFalse(SoundIds.isTrainSound("block/anvil_land"));
		assertFalse(SoundIds.isTrainSound("my_train_sound/horn"));
	}

	@Test
	void formatsDisplayNames() {
		assertEquals("Steam Whistle", SoundIds.displayName("train_sound/steam_whistle"));
		assertEquals("Uk Class 66 Horn", SoundIds.displayName("train_sound/uk/class_66_horn"));
		assertEquals("Horn", SoundIds.displayName("horn"));
	}
}
