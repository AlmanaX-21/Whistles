package me.almana.whistles;

import me.almana.whistles.block.TrainSoundPostBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllBlocks {

	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Whistles.ID);
	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Whistles.ID);

	public static final DeferredBlock<TrainSoundPostBlock> TRAIN_SOUND_POST =
		BLOCKS.register("train_sound_post", () -> new TrainSoundPostBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
			.noOcclusion()));

	public static final DeferredItem<BlockItem> TRAIN_SOUND_POST_ITEM =
		ITEMS.register("train_sound_post", () -> new BlockItem(TRAIN_SOUND_POST.get(), new Item.Properties()));

	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
	}
}
