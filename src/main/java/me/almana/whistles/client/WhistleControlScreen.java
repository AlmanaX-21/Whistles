package me.almana.whistles.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.Config;
import me.almana.whistles.block.SoundMode;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WhistleControlScreen extends Screen {

	private static final int CHAIN_WIDTH = 30;
	private static final int CHAIN_HEIGHT = 140;
	private static final int GAP = 30;
	private static final int RIGHT_MARGIN = 60;

	private final List<PullChainWidget> chains = new ArrayList<>();
	private UUID trainId;

	public WhistleControlScreen() {
		super(Component.empty());
	}

	@Override
	protected void init() {
		if (!(ControlsHandler.getContraption() instanceof CarriageContraptionEntity contraption)) {
			onClose();
			return;
		}
		trainId = contraption.trainId;
		Train train = Create.RAILWAYS.sided(null).trains.get(trainId);
		if (train == null) {
			onClose();
			return;
		}

		List<SoundMode> present = new ArrayList<>();
		for (SoundMode mode : SoundMode.values())
			if (TrainSoundSources.find(train, mode, minecraft.level) != null)
				present.add(mode);

		int totalWidth = present.size() * CHAIN_WIDTH + Math.max(0, present.size() - 1) * GAP;
		int left = width - RIGHT_MARGIN - totalWidth;
		int top = height / 2 - CHAIN_HEIGHT / 2;
		for (SoundMode mode : present) {
			chains.add(addRenderableWidget(new PullChainWidget(left, top, CHAIN_WIDTH, CHAIN_HEIGHT, mode)));
			left += CHAIN_WIDTH + GAP;
		}
	}

	@Override
	public void tick() {
		int range = Config.pitchRange();
		for (PullChainWidget chain : chains) {
			chain.easeTowardZero();
			TrainSoundInput.sendIfChanged(chain.mode(), trainId, chain.isActive(), chain.semitones(range));
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean handled = super.mouseReleased(mouseX, mouseY, button);
		for (PullChainWidget chain : chains)
			chain.forceRelease();
		return handled;
	}

	@Override
	public void onClose() {
		TrainSoundInput.releaseAll();
		minecraft.setScreen(null);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		for (PullChainWidget chain : chains) {
			Component label = Component.translatable("whistles.hud." + chain.mode()
				.getSerializedName());
			int centerX = chain.getX() + chain.getWidth() / 2;
			graphics.drawCenteredString(font, label, centerX, chain.getY() + chain.getHeight() + 6, 0xFFFFFF);
		}
	}
}
