package me.almana.whistles;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Whistles.ID)
public class Whistles {

	public static final String ID = "whistles";

	public Whistles(IEventBus modBus, ModContainer container) {
		AllBlocks.register(modBus);
		AllBlockEntities.register(modBus);
		AllCreativeTabs.register(modBus);
		modBus.addListener(AllPackets::register);
		Config.register(container);
	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
}
