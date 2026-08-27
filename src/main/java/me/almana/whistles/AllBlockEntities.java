package me.almana.whistles;

import me.almana.whistles.block.TrainSoundPostBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllBlockEntities {

	private static final DeferredRegister<BlockEntityType<?>> TYPES =
		DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Whistles.ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrainSoundPostBlockEntity>> TRAIN_SOUND_POST =
		TYPES.register("train_sound_post", () -> BlockEntityType.Builder
			.of(TrainSoundPostBlockEntity::new, AllBlocks.TRAIN_SOUND_POST.get())
			.build(null));

	public static void register(IEventBus bus) {
		TYPES.register(bus);
	}
}
