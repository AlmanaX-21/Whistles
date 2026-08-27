package me.almana.whistles.client;

import java.util.function.DoubleConsumer;

import me.almana.whistles.net.SetTrainSoundSettingsPacket;
import me.almana.whistles.sound.PitchCodec;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class TrainSoundSettingsScreen extends Screen {

	private static final int ROW_HEIGHT = 22;
	private static final int SLIDER_WIDTH = 200;
	private static final int SLIDER_HEIGHT = 20;
	private static final int TOP_MARGIN = 36;

	private final Screen parent;
	private final BlockPos pos;
	private int pitchRange;
	private float volume;
	private int hearingRange;
	private float leverVolumeInfluence;
	private float leverVolumeMin;
	private float leverVolumeMax;

	public TrainSoundSettingsScreen(Screen parent, BlockPos pos, TrainSoundSettings settings) {
		super(Component.translatable("whistles.gui.post_settings_title"));
		this.parent = parent;
		this.pos = pos;
		pitchRange = settings.pitchRange();
		volume = settings.volume();
		hearingRange = settings.hearingRange();
		leverVolumeInfluence = settings.leverVolumeInfluence();
		leverVolumeMin = settings.leverVolumeMin();
		leverVolumeMax = settings.leverVolumeMax();
	}

	@Override
	protected void init() {
		int x = width / 2 - SLIDER_WIDTH / 2;
		int y = TOP_MARGIN;
		y = addSlider(x, y, "whistles.gui.pitch_range", 1, PitchCodec.MAX_RANGE, 0, pitchRange,
			value -> pitchRange = (int) value);
		y = addSlider(x, y, "whistles.gui.volume", 0, 1, 2, volume, value -> volume = (float) value);
		y = addSlider(x, y, "whistles.gui.hearing_range", 8, 128, 0, hearingRange,
			value -> hearingRange = (int) value);
		y = addSlider(x, y, "whistles.gui.lever_volume_influence", .1, 5, 2, leverVolumeInfluence,
			value -> leverVolumeInfluence = (float) value);
		y = addSlider(x, y, "whistles.gui.lever_volume_min", 0, 100, 0, leverVolumeMin,
			value -> leverVolumeMin = (float) value);
		y = addSlider(x, y, "whistles.gui.lever_volume_max", 0, 100, 0, leverVolumeMax,
			value -> leverVolumeMax = (float) value);

		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> saveAndClose())
			.bounds(x, y + 6, SLIDER_WIDTH, SLIDER_HEIGHT)
			.build());
	}

	private int addSlider(int x, int y, String labelKey, double min, double max, int decimals, double current,
		DoubleConsumer apply) {
		addRenderableWidget(new ConfigValueSlider(x, y, SLIDER_WIDTH, SLIDER_HEIGHT, labelKey, min, max, decimals,
			current, apply));
		return y + ROW_HEIGHT;
	}

	private void saveAndClose() {
		TrainSoundSettings settings = new TrainSoundSettings(pitchRange, volume, hearingRange, leverVolumeInfluence,
			leverVolumeMin, leverVolumeMax);
		PacketDistributor.sendToServer(new SetTrainSoundSettingsPacket(pos, settings));
		minecraft.setScreen(parent);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
	}
}
