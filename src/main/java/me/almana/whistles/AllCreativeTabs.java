package me.almana.whistles;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllCreativeTabs {

	private static final DeferredRegister<CreativeModeTab> TABS =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Whistles.ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup." + Whistles.ID))
		.icon(() -> AllBlocks.TRAIN_SOUND_POST_ITEM.get()
			.getDefaultInstance())
		.displayItems((params, output) -> output.accept(AllBlocks.TRAIN_SOUND_POST_ITEM.get()))
		.build());

	public static void register(IEventBus bus) {
		TABS.register(bus);
	}
}
