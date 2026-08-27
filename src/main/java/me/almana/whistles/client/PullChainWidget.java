package me.almana.whistles.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.almana.whistles.Config;
import me.almana.whistles.Whistles;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PullChainWidget extends AbstractWidget {

	private static final float EASE_STEP = 0.15f;
	private static final float DEADZONE = 0.02f;
	private static final float GHOST_ALPHA = 0.2f;
	private static final int ROPE_WIDTH = 20;
	private static final int ROPE_TEXTURE_HEIGHT = 20;
	private static final int ROPE_LEFT = 5;
	private static final int HANDLE_TEXTURE_TOP = 20;
	private static final int HANDLE_TEXTURE_HEIGHT = 12;
	private static final ResourceLocation PULLEY = Whistles.asResource("textures/gui/pulley.png");
	private static final ResourceLocation PULLEY_ROPE = Whistles.asResource("textures/gui/pulley_rope.png");

	private final int sourceIndex;
	private final SoundMode mode;
	private final TrainSoundSettings settings;
	private final int travel;
	private float pull;
	private boolean dragging;
	private double grabOffset;
	private int hangTicks;

	public PullChainWidget(int x, int y, int travel, int sourceIndex, SoundMode mode, TrainSoundSettings settings) {
		super(x, y, PullChainLayout.PULLEY_WIDTH, travel + PullChainLayout.PULLEY_HEIGHT,
			Component.translatable("whistles.hud." + mode.getSerializedName()));
		this.sourceIndex = sourceIndex;
		this.mode = mode;
		this.settings = settings;
		this.travel = travel;
	}

	public int sourceIndex() {
		return sourceIndex;
	}

	public SoundMode mode() {
		return mode;
	}

	public boolean isActive() {
		return pull > DEADZONE;
	}

	public TrainSoundSettings settings() {
		return settings;
	}

	public float semitones() {
		return -settings.pitchRange() + pull * 2 * settings.pitchRange();
	}

	public void forceRelease() {
		release();
	}

	private void release() {
		if (dragging) {
			dragging = false;
			if (pull > 0)
				hangTicks = Config.hangTicks();
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

	private int pulleyTop() {
		return getY() + Math.round(pull * travel);
	}

	private void updatePull(double mouseY) {
		pull = PullChainLayout.pullForMouse(mouseY, grabOffset, getY(), travel);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return active && visible && PullChainLayout.isOverHandle(mouseX, mouseY, getX(), getY(), pull, travel);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isValidClickButton(button) || !isMouseOver(mouseX, mouseY))
			return false;
		dragging = true;
		grabOffset = mouseY - pulleyTop();
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
		RenderSystem.enableBlend();
		graphics.setColor(1, 1, 1, GHOST_ALPHA);
		graphics.blit(PULLEY, getX(), getY() + travel + HANDLE_TEXTURE_TOP, 0, HANDLE_TEXTURE_TOP,
			PullChainLayout.PULLEY_WIDTH, HANDLE_TEXTURE_HEIGHT, PullChainLayout.PULLEY_WIDTH,
			PullChainLayout.PULLEY_HEIGHT);
		graphics.setColor(1, 1, 1, 1);
		RenderSystem.disableBlend();

		int ropeTop = getY();
		int remaining = pulleyTop() - ropeTop + HANDLE_TEXTURE_TOP;
		while (remaining > 0) {
			int segmentHeight = PullChainLayout.ropeSegmentHeight(remaining);
			graphics.blit(PULLEY_ROPE, getX() + ROPE_LEFT, ropeTop, 0, 0, ROPE_WIDTH, segmentHeight,
				ROPE_WIDTH, ROPE_TEXTURE_HEIGHT);
			ropeTop += segmentHeight;
			remaining -= segmentHeight;
		}
		graphics.blit(PULLEY, getX(), pulleyTop() + HANDLE_TEXTURE_TOP, 0, HANDLE_TEXTURE_TOP,
			PullChainLayout.PULLEY_WIDTH, HANDLE_TEXTURE_HEIGHT, PullChainLayout.PULLEY_WIDTH,
			PullChainLayout.PULLEY_HEIGHT);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
