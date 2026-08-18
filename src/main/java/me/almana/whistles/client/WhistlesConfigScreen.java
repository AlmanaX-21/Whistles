package me.almana.whistles.client;

import me.almana.whistles.Config;
import me.almana.whistles.sound.PitchCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WhistlesConfigScreen extends Screen {

	private final Screen parent;

	public WhistlesConfigScreen(Screen parent) {
		super(Component.translatable("whistles.gui.config_title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		PitchRangeSlider slider = new PitchRangeSlider(width / 2 - 100, height / 2 - 10, 200, 20,
			Config.pitchRange());
		slider.active = Config.pitchRangeEditable();
		addRenderableWidget(slider);

		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(width / 2 - 100, height / 2 + 20, 200, 20)
			.build());
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, height / 2 - 40, 0xFFFFFF);
		if (!Config.pitchRangeEditable())
			graphics.drawCenteredString(font, Component.translatable("whistles.gui.config_needs_world"),
				width / 2, height / 2 + 46, 0xB0B0B0);
	}

	private static class PitchRangeSlider extends AbstractSliderButton {

		PitchRangeSlider(int x, int y, int w, int h, int current) {
			super(x, y, w, h, Component.empty(), normalize(current));
			updateMessage();
		}

		private static double normalize(int semitones) {
			return (semitones - 1) / (double) (PitchCodec.MAX_RANGE - 1);
		}

		private int semitones() {
			return 1 + (int) Math.round(value * (PitchCodec.MAX_RANGE - 1));
		}

		@Override
		protected void updateMessage() {
			setMessage(Component.translatable("whistles.gui.pitch_range", semitones()));
		}

		@Override
		protected void applyValue() {
			Config.setPitchRange(semitones());
		}
	}
}
