package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

class TrainSoundSourcesTest {

	@Test
	void returnsTheDeterministicFirstThreePositions() {
		Set<BlockPos> positions = Set.of(
			new BlockPos(2, 0, 0),
			new BlockPos(-1, 9, 5),
			new BlockPos(-1, 3, 8),
			new BlockPos(0, 0, 0));

		assertEquals(List.of(
			new BlockPos(-1, 3, 8),
			new BlockPos(-1, 9, 5),
			new BlockPos(0, 0, 0)), TrainSoundSources.orderPositions(positions, 3));
	}

	@Test
	void readsSettingsFromContraptionData() {
		TrainSoundSettings expected = new TrainSoundSettings(6, .7f, 80, 1.4f, 15, 85);
		CompoundTag tag = new CompoundTag();
		tag.put("Settings", expected.write());
		StructureBlockInfo info = new StructureBlockInfo(BlockPos.ZERO, null, tag);

		assertEquals(expected, TrainSoundSources.settingsOf(info, true));
	}

	@Test
	void snapshotsDefaultsIntoLegacyContraptionData() {
		TrainSoundSettings defaults = new TrainSoundSettings(12, 1, 64, 1, 0, 100);
		CompoundTag tag = new CompoundTag();
		StructureBlockInfo info = new StructureBlockInfo(BlockPos.ZERO, null, tag);

		assertEquals(defaults, TrainSoundSources.settingsOf(info, true));
		assertEquals(defaults, TrainSoundSettings.read(tag.getCompound("Settings")));
	}

	@Test
	void doesNotWriteClientFallbackIntoLegacyContraptionData() {
		CompoundTag tag = new CompoundTag();
		StructureBlockInfo info = new StructureBlockInfo(BlockPos.ZERO, null, tag);

		assertEquals(new TrainSoundSettings(12, 1, 64, 1, 0, 100), TrainSoundSources.settingsOf(info, false));
		assertFalse(tag.contains("Settings"));
	}
}
