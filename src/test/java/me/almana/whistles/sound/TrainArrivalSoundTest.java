package me.almana.whistles.sound;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import me.almana.whistles.block.SoundMode;
import me.almana.whistles.client.TrainSoundSources.Source;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

class TrainArrivalSoundTest {

	private static final AtomicInteger NEXT_CARRIAGE = new AtomicInteger();

	@Test
	void replacesCreateArrivalOnlyInsideItsApproachWindow() {
		assertTrue(TrainArrivalSound.shouldReplace(true, 59.99, true, true));
		assertTrue(TrainArrivalSound.shouldReplace(true, -59.99, true, true));
		assertFalse(TrainArrivalSound.shouldReplace(true, 60, true, true));
		assertFalse(TrainArrivalSound.shouldReplace(false, 20, true, true));
		assertFalse(TrainArrivalSound.shouldReplace(true, 20, false, true));
		assertFalse(TrainArrivalSound.shouldReplace(true, 20, true, false));
	}

	@Test
	void selectedWhistleOverridesPositionOrder() {
		List<Source> sources = List.of(
			source(SoundMode.HORN, false),
			source(SoundMode.WHISTLE, true),
			source(SoundMode.HORN, true));

		assertEquals(1, TrainArrivalSound.preferredSourceIndex(sources));
	}

	@Test
	void firstSoundPostIsTheFallback() {
		assertEquals(0, TrainArrivalSound.preferredSourceIndex(List.of(
			source(SoundMode.WHISTLE, false),
			source(SoundMode.HORN, false))));
		assertEquals(0, TrainArrivalSound.preferredSourceIndex(List.of(
			source(SoundMode.WHISTLE, false))));
	}

	@Test
	void fallbackDoesNotDependOnAControllableHorn() {
		assertEquals(0, TrainArrivalSound.preferredSourceIndex(List.of(
			source(SoundMode.WHISTLE, false),
			source(SoundMode.WHISTLE, false),
			source(SoundMode.WHISTLE, false),
			source(SoundMode.HORN, false))));
	}

	@Test
	void normalizationKeepsFirstSupportedSelectionAndClearsOthers() {
		List<Source> sources = List.of(
			source(SoundMode.WHISTLE, true),
			source(SoundMode.HORN, true),
			source(SoundMode.HORN, false),
			source(SoundMode.HORN, true));

		assertEquals(0, TrainArrivalSound.normalizeSelectedSource(sources));
		assertTrue(sources.get(0).automaticArrival());
		assertFalse(sources.get(1).automaticArrival());
		assertFalse(sources.get(3).automaticArrival());
	}

	@Test
	void mostRecentlyEnabledPostWinsAcrossTheWholeTrain() {
		List<Source> sources = List.of(
			source(SoundMode.WHISTLE, true, 100),
			source(SoundMode.HORN, true, 400),
			source(SoundMode.WHISTLE, true, 300),
			source(SoundMode.HORN, true, 500));

		assertEquals(3, TrainArrivalSound.normalizeSelectedSource(sources));
		assertFalse(sources.get(0).automaticArrival());
		assertFalse(sources.get(1).automaticArrival());
		assertFalse(sources.get(2).automaticArrival());
		assertTrue(sources.get(3).automaticArrival());
	}

	@Test
	void keepsDimensionalCopiesOfTheWinningPostSelectedTogether() {
		BlockPos firstPost = new BlockPos(1, 2, 3);
		BlockPos secondPost = new BlockPos(4, 5, 6);
		List<Source> sources = List.of(
			source(0, firstPost, true, 100),
			source(0, firstPost, true, 100),
			source(0, secondPost, true, 200),
			source(0, secondPost, true, 200));

		assertEquals(2, TrainArrivalSound.normalizeSelectedSource(sources));
		assertFalse(sources.get(0).automaticArrival());
		assertFalse(sources.get(1).automaticArrival());
		assertTrue(sources.get(2).automaticArrival());
		assertTrue(sources.get(3).automaticArrival());
	}

	private static Source source(SoundMode mode, boolean automaticArrival) {
		return source(mode, automaticArrival, 0);
	}

	private static Source source(SoundMode mode, boolean automaticArrival, long automaticArrivalOrder) {
		return source(NEXT_CARRIAGE.getAndIncrement(), BlockPos.ZERO, automaticArrival, automaticArrivalOrder, mode);
	}

	private static Source source(int carriageIndex, BlockPos localPos, boolean automaticArrival,
		long automaticArrivalOrder) {
		return source(carriageIndex, localPos, automaticArrival, automaticArrivalOrder, SoundMode.HORN);
	}

	private static Source source(int carriageIndex, BlockPos localPos, boolean automaticArrival,
		long automaticArrivalOrder, SoundMode mode) {
		CompoundTag data = new CompoundTag();
		data.putLong("AutomaticArrivalOrder", automaticArrivalOrder);
		Source source = new Source(carriageIndex, null, localPos,
			new ResourceLocation("whistles", "train_sound/test"), mode,
			new TrainSoundSettings(12, 1, 64, 1, 0, 100), data);
		source.setAutomaticArrival(automaticArrival);
		return source;
	}
}
