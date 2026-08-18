package me.almana.whistles;

import me.almana.whistles.block.TrainSoundPostBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllBlockEntities {

	private static final DeferredRegister<BlockEntityType<?>> TYPES =
		DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Whistles.ID);

	public static final RegistryObject<BlockEntityType<TrainSoundPostBlockEntity>> TRAIN_SOUND_POST =
		TYPES.register("train_sound_post", () -> BlockEntityType.Builder
			.of(TrainSoundPostBlockEntity::new, AllBlocks.TRAIN_SOUND_POST.get())
			.build(null));

	public static void register(IEventBus bus) {
		TYPES.register(bus);
	}
}
