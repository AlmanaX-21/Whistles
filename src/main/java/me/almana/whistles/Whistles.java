package me.almana.whistles;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Whistles.ID)
public class Whistles {

	public static final String ID = "whistles";

	public Whistles() {
		IEventBus modBus = FMLJavaModLoadingContext.get()
			.getModEventBus();
		AllBlocks.register(modBus);
		AllBlockEntities.register(modBus);
		AllCreativeTabs.register(modBus);
		modBus.addListener((FMLCommonSetupEvent e) -> AllPackets.register());
		Config.register();
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
