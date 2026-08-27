package me.almana.whistles.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WhistleControlScreen extends Screen {

	private final List<PullChainWidget> chains = new ArrayList<>();
	private UUID trainId;

	public WhistleControlScreen() {
		super(Component.empty());
	}

	@Override
	protected void init() {
		chains.clear();
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

		List<TrainSoundSources.Source> sources = TrainSoundSources.find(train, minecraft.level);
		int travel = PullChainLayout.travel(height);
		int left = PullChainLayout.controlsLeft(width, sources.size());
		for (int sourceIndex = 0; sourceIndex < sources.size(); sourceIndex++) {
			TrainSoundSources.Source source = sources.get(sourceIndex);
			PullChainWidget chain = addRenderableWidget(new PullChainWidget(left, 0, travel, sourceIndex, source.mode(),
				source.settings()));
			chains.add(chain);
			left += PullChainLayout.PULLEY_WIDTH + PullChainLayout.GAP;
		}
	}

	@Override
	public void tick() {
		for (PullChainWidget chain : chains) {
			chain.easeTowardZero();
			TrainSoundInput.sendIfChanged(chain.sourceIndex(), trainId, chain.isActive(), chain.semitones(),
				chain.settings());
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
	protected void renderBlurredBackground(float partialTick) {
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		for (PullChainWidget chain : chains) {
			Component label = Component.translatable("whistles.hud." + chain.mode()
				.getSerializedName());
			int centerX = chain.getX() + chain.getWidth() / 2;
			graphics.drawCenteredString(font, label, centerX,
				chain.getY() + chain.getHeight() + PullChainLayout.LABEL_GAP, 0xFFFFFF);
		}
	}
}
