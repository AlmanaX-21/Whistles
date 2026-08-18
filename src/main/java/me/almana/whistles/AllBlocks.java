package me.almana.whistles;

import me.almana.whistles.block.TrainSoundPostBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllBlocks {

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Whistles.ID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Whistles.ID);

	public static final RegistryObject<TrainSoundPostBlock> TRAIN_SOUND_POST =
		BLOCKS.register("train_sound_post", () -> new TrainSoundPostBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
			.noOcclusion()));

	public static final RegistryObject<BlockItem> TRAIN_SOUND_POST_ITEM =
		ITEMS.register("train_sound_post", () -> new BlockItem(TRAIN_SOUND_POST.get(), new Item.Properties()));

	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
	}
}
