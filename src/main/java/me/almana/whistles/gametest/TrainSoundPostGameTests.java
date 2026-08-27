package me.almana.whistles.gametest;

import me.almana.whistles.AllBlocks;
import me.almana.whistles.Whistles;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.block.TrainSoundPostBlock;
import me.almana.whistles.block.TrainSoundPostBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Whistles.ID)
@PrefixGameTestTemplate(false)
public class TrainSoundPostGameTests {

	@GameTest(templateNamespace = "minecraft", template = "bastion/mobs/empty")
	public static void postUsesWhistleDefaults(GameTestHelper helper) {
		BlockPos pos = BlockPos.ZERO;
		helper.setBlock(pos, AllBlocks.TRAIN_SOUND_POST.get());
		helper.assertBlockProperty(pos, TrainSoundPostBlock.MODE, SoundMode.WHISTLE);
		TrainSoundPostBlockEntity post = helper.getBlockEntity(pos);
		helper.assertTrue(post.getSound().equals(TrainSoundPostBlockEntity.DEFAULT_WHISTLE),
			"post did not use the default whistle sound");
		helper.succeed();
	}
}
