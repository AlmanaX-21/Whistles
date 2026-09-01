package me.almana.whistles.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import me.almana.whistles.block.SoundMode;
import me.almana.whistles.client.TrainSoundSources.Source;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

class TramwaysWhistleTest {

	@Test
	void automaticArrivalPostDrivesTheTramwaysWhistle() {
		assertEquals(1, TramwaysWhistle.sourceIndex(List.of(source(false), source(true), source(false))));
	}

	@Test
	void firstPostIsTheFallbackWithoutAnArrivalSelection() {
		assertEquals(0, TramwaysWhistle.sourceIndex(List.of(source(false), source(false))));
	}

	@Test
	void noPostKeepsTheNativeTramwaysWhistle() {
		assertEquals(-1, TramwaysWhistle.sourceIndex(List.of()));
	}

	@Test
	void automaticArrivalSelectionSpansDimensionSourceGroups() {
		List<Source> sources = TramwaysWhistle.combineSources(List.of(
			List.of(source(false), source(false)),
			List.of(source(true))));

		assertEquals(2, TramwaysWhistle.sourceIndex(sources));
	}

	@Test
	void fallbackUsesTrainSourceOrderAcrossDimensions() {
		Source later = source(2, new BlockPos(0, 0, 0), false);
		Source first = source(0, new BlockPos(4, 1, 2), false);
		List<Source> sources = TramwaysWhistle.combineSources(List.of(List.of(later), List.of(first)));

		assertSame(first, sources.get(TramwaysWhistle.sourceIndex(sources)));
	}

	private static Source source(boolean automaticArrival) {
		return source(0, BlockPos.ZERO, automaticArrival);
	}

	private static Source source(int carriageIndex, BlockPos localPos, boolean automaticArrival) {
		CompoundTag data = new CompoundTag();
		data.putBoolean("AutomaticArrival", automaticArrival);
		return new Source(carriageIndex, null, localPos, new ResourceLocation("whistles", "train_sound/test"),
			SoundMode.WHISTLE, new TrainSoundSettings(12, 1, 64, 1, 0, 100), data);
	}
}
