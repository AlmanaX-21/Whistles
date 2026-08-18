package me.almana.whistles.client;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import me.almana.whistles.Config;
import me.almana.whistles.sound.PitchCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WhistlesConfigScreen extends Screen {

	private static final int ROW_HEIGHT = 22;
	private static final int SLIDER_WIDTH = 200;
	private static final int SLIDER_HEIGHT = 20;
	private static final int TOP_MARGIN = 36;

	private final Screen parent;
	private int doneY;

	public WhistlesConfigScreen(Screen parent) {
		super(Component.translatable("whistles.gui.config_title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		boolean serverEditable = Config.serverConfigEditable();
		int x = width / 2 - SLIDER_WIDTH / 2;
		int y = TOP_MARGIN;

		y = addSlider(x, y, "whistles.gui.pitch_range", 1, PitchCodec.MAX_RANGE, 0, serverEditable,
			Config::pitchRange, v -> Config.setPitchRange((int) v));
		y = addSlider(x, y, "whistles.gui.volume", 0.0, 1.0, 2, serverEditable, Config::volume, Config::setVolume);
		y = addSlider(x, y, "whistles.gui.hearing_range", 8, 128, 0, serverEditable, Config::hearingRange,
			v -> Config.setHearingRange((int) v));
		y = addSlider(x, y, "whistles.gui.lever_volume_influence", 0.1, 5.0, 2, serverEditable,
			Config::leverVolumeInfluence, Config::setLeverVolumeInfluence);
		y = addSlider(x, y, "whistles.gui.lever_volume_min", 0, 100, 0, serverEditable, Config::leverVolumeMin,
			Config::setLeverVolumeMin);
		y = addSlider(x, y, "whistles.gui.lever_volume_max", 0, 100, 0, serverEditable, Config::leverVolumeMax,
			Config::setLeverVolumeMax);
		y = addSlider(x, y, "whistles.gui.sweep_ticks", 5, 400, 0, true, Config::sweepTicks,
			v -> Config.setSweepTicks((int) v));
		y = addSlider(x, y, "whistles.gui.hang_ticks", 0, 200, 0, true, Config::hangTicks,
			v -> Config.setHangTicks((int) v));

		doneY = y + 6;
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(x, doneY, SLIDER_WIDTH, SLIDER_HEIGHT)
			.build());
	}

	private int addSlider(int x, int y, String labelKey, double min, double max, int decimals, boolean editable,
		DoubleSupplier current, DoubleConsumer apply) {
		ValueSlider slider = new ValueSlider(x, y, SLIDER_WIDTH, SLIDER_HEIGHT, labelKey, min, max, decimals,
			current.getAsDouble(), apply);
		slider.active = editable;
		addRenderableWidget(slider);
		return y + ROW_HEIGHT;
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
		if (!Config.serverConfigEditable())
			graphics.drawCenteredString(font, Component.translatable("whistles.gui.config_needs_world"), width / 2,
				doneY + 26, 0xB0B0B0);
	}

	private static class ValueSlider extends AbstractSliderButton {

		private final String labelKey;
		private final double min;
		private final double max;
		private final int decimals;
		private final DoubleConsumer apply;

		ValueSlider(int x, int y, int w, int h, String labelKey, double min, double max, int decimals, double current,
			DoubleConsumer apply) {
			super(x, y, w, h, Component.empty(), (current - min) / (max - min));
			this.labelKey = labelKey;
			this.min = min;
			this.max = max;
			this.decimals = decimals;
			this.apply = apply;
			updateMessage();
		}

		private double current() {
			double raw = min + value * (max - min);
			double scale = Math.pow(10, decimals);
			return Math.round(raw * scale) / scale;
		}

		@Override
		protected void updateMessage() {
			double shown = current();
			setMessage(Component.translatable(labelKey, decimals == 0 ? (long) shown : shown));
		}

		@Override
		protected void applyValue() {
			apply.accept(current());
		}
	}
}
