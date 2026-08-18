package me.almana.whistles;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AllCreativeTabs {

	private static final DeferredRegister<CreativeModeTab> TABS =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Whistles.ID);

	public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup." + Whistles.ID))
		.icon(() -> AllBlocks.TRAIN_SOUND_POST_ITEM.get()
			.getDefaultInstance())
		.displayItems((params, output) -> output.accept(AllBlocks.TRAIN_SOUND_POST_ITEM.get()))
		.build());

	public static void register(IEventBus bus) {
		TABS.register(bus);
	}
}
