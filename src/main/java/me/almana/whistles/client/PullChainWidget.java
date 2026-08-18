package me.almana.whistles.client;

import me.almana.whistles.block.SoundMode;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class PullChainWidget extends AbstractWidget {

	private static final float EASE_STEP = 0.15f;
	private static final float DEADZONE = 0.02f;
	private static final int HANG_TICKS = 20;

	private final SoundMode mode;
	private float pull;
	private boolean dragging;
	private int hangTicks;

	public PullChainWidget(int x, int y, int width, int height, SoundMode mode) {
		super(x, y, width, height, Component.translatable("whistles.hud." + mode.getSerializedName()));
		this.mode = mode;
	}

	public SoundMode mode() {
		return mode;
	}

	public boolean isActive() {
		return pull > DEADZONE;
	}

	public float semitones(int range) {
		return -range + pull * 2 * range;
	}

	public void forceRelease() {
		release();
	}

	private void release() {
		if (dragging) {
			dragging = false;
			if (pull > 0)
				hangTicks = HANG_TICKS;
		}
	}

	public void easeTowardZero() {
		if (dragging)
			return;
		if (hangTicks > 0) {
			hangTicks--;
			return;
		}
		if (pull > 0)
			pull = Math.max(0, pull - EASE_STEP);
	}

	private void updatePull(double mouseY) {
		pull = (float) Math.max(0, Math.min(1, (mouseY - getY()) / getHeight()));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isValidClickButton(button) || !isMouseOver(mouseX, mouseY))
			return false;
		dragging = true;
		updatePull(mouseY);
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (!dragging)
			return false;
		updatePull(mouseY);
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		release();
		return true;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		int left = getX();
		int top = getY();
		int right = left + getWidth();
		int bottom = top + getHeight();
		int handleY = top + Math.round(pull * getHeight());

		graphics.fill(left, top, right, bottom, 0x50000000);
		if (isActive())
			graphics.fill(left, top, right, handleY, 0x60FFD87F);
		int colour = isActive() ? 0xFFFFD87F : 0xFFA0A0A0;
		graphics.fill(left, Math.min(handleY, bottom - 3), right, Math.min(handleY + 3, bottom), colour);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
