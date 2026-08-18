package me.almana.whistles.client;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import me.almana.whistles.Whistles;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

	@Mod.EventBusSubscriber(modid = Whistles.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBus {

		@SubscribeEvent
		public static void registerKeys(RegisterKeyMappingsEvent event) {
			for (KeyMapping key : AllKeys.all())
				event.register(key);
		}

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ModLoadingContext.get()
				.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new WhistlesConfigScreen(parent)));
		}
	}

	@Mod.EventBusSubscriber(modid = Whistles.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeBus {

		@SubscribeEvent
		public static void tick(TickEvent.ClientTickEvent event) {
			if (event.phase != TickEvent.Phase.END)
				return;
			TrainSoundInput.tick();
			TrainSounds.tick();

			while (AllKeys.OPEN_CONTROLS.consumeClick()) {
				Minecraft mc = Minecraft.getInstance();
				if (mc.screen instanceof WhistleControlScreen)
					mc.screen.onClose();
				else if (mc.screen == null && ControlsHandler.getContraption() instanceof CarriageContraptionEntity)
					mc.setScreen(new WhistleControlScreen());
			}
		}

		@SubscribeEvent
		public static void levelUnloaded(LevelEvent.Unload event) {
			if (event.getLevel()
				.isClientSide())
				TrainSounds.clear();
		}
	}
}
