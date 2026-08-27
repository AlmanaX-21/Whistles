package me.almana.whistles.client;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import me.almana.whistles.Whistles;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.level.LevelEvent;

public class ClientSetup {

	@EventBusSubscriber(modid = Whistles.ID, value = Dist.CLIENT)
	public static class ModBus {

		@SubscribeEvent
		public static void registerKeys(RegisterKeyMappingsEvent event) {
			for (KeyMapping key : AllKeys.all())
				event.register(key);
		}

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ModLoadingContext.get()
				.getActiveContainer()
				.registerExtensionPoint(IConfigScreenFactory.class,
					(container, parent) -> new WhistlesConfigScreen(parent));
		}
	}

	@EventBusSubscriber(modid = Whistles.ID, value = Dist.CLIENT)
	public static class ForgeBus {

		@SubscribeEvent
		public static void tick(ClientTickEvent.Post event) {
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
