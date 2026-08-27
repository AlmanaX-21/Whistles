package me.almana.whistles.client;

import java.util.Locale;

import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.net.SetTrainSoundPacket;
import me.almana.whistles.sound.SoundIds;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class TrainSoundSelectScreen extends Screen {

	private final BlockPos pos;
	private ResourceLocation selected;
	private boolean automaticArrival;
	private Button automaticArrivalButton;
	private EditBox search;
	private SoundList list;

	private TrainSoundSelectScreen(BlockPos pos, ResourceLocation selected, boolean automaticArrival) {
		super(Component.translatable("whistles.gui.select_sound"));
		this.pos = pos;
		this.selected = selected;
		this.automaticArrival = automaticArrival;
	}

	public static void open(BlockPos pos) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
			mc.setScreen(new TrainSoundSelectScreen(pos, be.getSound(), be.isAutomaticArrival()));
	}

	@Override
	protected void init() {
		list = new SoundList();
		addRenderableWidget(list);

		search = new EditBox(font, TrainSoundSelectLayout.contentLeft(width), 28,
			TrainSoundSelectLayout.CONTENT_WIDTH, 18, Component.translatable("whistles.gui.search"));
		search.setHint(Component.translatable("whistles.gui.search"));
		search.setResponder(text -> list.refresh(text));
		addRenderableWidget(search);

		addRenderableWidget(Button.builder(Component.translatable("whistles.gui.preview"), b -> preview())
			.bounds(TrainSoundSelectLayout.previewButtonLeft(width), TrainSoundSelectLayout.buttonTop(height),
				TrainSoundSelectLayout.BUTTON_WIDTH, TrainSoundSelectLayout.BUTTON_HEIGHT)
			.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(TrainSoundSelectLayout.doneButtonLeft(width), TrainSoundSelectLayout.buttonTop(height),
				TrainSoundSelectLayout.BUTTON_WIDTH, TrainSoundSelectLayout.BUTTON_HEIGHT)
			.build());
		automaticArrivalButton = addRenderableWidget(Button.builder(automaticArrivalLabel(), button -> toggleAutomatic())
			.bounds(TrainSoundSelectLayout.previewButtonLeft(width), TrainSoundSelectLayout.settingsButtonTop(height),
				TrainSoundSelectLayout.BUTTON_WIDTH, TrainSoundSelectLayout.BUTTON_HEIGHT)
			.build());
		addRenderableWidget(Button.builder(Component.translatable("whistles.gui.settings"), button -> openSettings())
			.bounds(TrainSoundSelectLayout.settingsButtonLeft(width), TrainSoundSelectLayout.settingsButtonTop(height),
				TrainSoundSelectLayout.BUTTON_WIDTH, TrainSoundSelectLayout.BUTTON_HEIGHT)
			.build());

		list.refresh("");
	}

	private void preview() {
		minecraft.getSoundManager()
			.play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(selected), 1f));
	}

	private void openSettings() {
		if (minecraft.level.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
			minecraft.setScreen(new TrainSoundSettingsScreen(this, pos, be.getSettings()));
	}

	private void toggleAutomatic() {
		automaticArrival = !automaticArrival;
		automaticArrivalButton.setMessage(automaticArrivalLabel());
		PacketDistributor.sendToServer(new SetTrainSoundPacket(pos, selected, automaticArrival));
	}

	private Component automaticArrivalLabel() {
		return Component.translatable(automaticArrival ? "whistles.gui.auto_arrival_on"
			: "whistles.gui.auto_arrival_off");
	}

	@Override
	public void onClose() {
		PacketDistributor.sendToServer(new SetTrainSoundPacket(pos, selected, automaticArrival));
		super.onClose();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.fill(TrainSoundSelectLayout.contentLeft(width) - TrainSoundSelectLayout.PANEL_PADDING,
			TrainSoundSelectLayout.LIST_TOP - 4,
			TrainSoundSelectLayout.contentRight(width) + TrainSoundSelectLayout.PANEL_PADDING,
			TrainSoundSelectLayout.listBottom(height) + 4, 0x90000000);
		super.render(graphics, mouseX, mouseY, partialTick);

		graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
		graphics.drawCenteredString(font, Component.translatable("whistles.gui.selected",
			Component.literal(SoundIds.displayName(selected.getPath())).withStyle(ChatFormatting.AQUA)),
			width / 2, TrainSoundSelectLayout.selectedLabelTop(height), 0xB0B0B0);
	}

	private class SoundList extends ObjectSelectionList<SoundList.Entry> {

		SoundList() {
			super(TrainSoundSelectScreen.this.minecraft, TrainSoundSelectScreen.this.width,
				TrainSoundSelectLayout.listBottom(TrainSoundSelectScreen.this.height)
					- TrainSoundSelectLayout.LIST_TOP,
				TrainSoundSelectLayout.LIST_TOP,
				TrainSoundSelectLayout.ROW_HEIGHT);
		}

		@Override
		protected void renderListBackground(GuiGraphics graphics) {
		}

		@Override
		protected void renderListSeparators(GuiGraphics graphics) {
		}

		void refresh(String filter) {
			String needle = filter.toLowerCase(Locale.ROOT);
			clearEntries();
			for (ResourceLocation id : TrainSoundLibrary.available()) {
				if (!id.toString()
					.toLowerCase(Locale.ROOT)
					.contains(needle))
					continue;
				Entry entry = new Entry(id);
				addEntry(entry);
				if (id.equals(selected))
					setSelected(entry);
			}
		}

		@Override
		public void setSelected(Entry entry) {
			super.setSelected(entry);
			if (entry != null)
				selected = entry.id;
		}

		@Override
		public int getRowWidth() {
			return TrainSoundSelectLayout.CONTENT_WIDTH;
		}

		@Override
		protected int getScrollbarPosition() {
			return TrainSoundSelectLayout.contentRight(TrainSoundSelectScreen.this.width) + 10;
		}

		class Entry extends ObjectSelectionList.Entry<Entry> {

			private final ResourceLocation id;
			private final Component label;

			Entry(ResourceLocation id) {
				this.id = id;
				this.label = Component.literal(SoundIds.displayName(id.getPath()));
			}

			@Override
			public void render(GuiGraphics graphics, int index, int top, int left, int rowWidth, int rowHeight,
				int mouseX, int mouseY, boolean hovering, float partialTick) {
				graphics.drawString(font, label, left + 5, top + TrainSoundSelectLayout.NAME_TOP, 0xFFFFFF);
				graphics.drawString(font, id.toString(), left + 5,
					top + TrainSoundSelectLayout.IDENTIFIER_TOP, 0x8899AA, false);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				SoundList.this.setSelected(this);
				return true;
			}

			@Override
			public Component getNarration() {
				return label;
			}
		}
	}
}
