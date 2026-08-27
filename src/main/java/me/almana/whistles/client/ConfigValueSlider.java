package me.almana.whistles.client;

import java.util.function.DoubleConsumer;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

class ConfigValueSlider extends AbstractSliderButton {

	private final String labelKey;
	private final double min;
	private final double max;
	private final int decimals;
	private final DoubleConsumer apply;

	ConfigValueSlider(int x, int y, int width, int height, String labelKey, double min, double max, int decimals,
		double current, DoubleConsumer apply) {
		super(x, y, width, height, Component.empty(), (current - min) / (max - min));
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
