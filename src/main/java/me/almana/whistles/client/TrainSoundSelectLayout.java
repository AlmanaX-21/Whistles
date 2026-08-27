package me.almana.whistles.client;

final class TrainSoundSelectLayout {

	static final int CONTENT_WIDTH = 200;
	static final int ROW_HEIGHT = 28;
	static final int NAME_TOP = 3;
	static final int IDENTIFIER_TOP = 15;
	static final int LIST_TOP = 56;
	static final int LIST_BOTTOM_MARGIN = 88;
	static final int BUTTON_WIDTH = 98;
	static final int BUTTON_HEIGHT = 20;
	static final int BUTTON_GAP = 4;
	static final int PANEL_PADDING = 6;

	private TrainSoundSelectLayout() {
	}

	static int contentLeft(int screenWidth) {
		return (screenWidth - CONTENT_WIDTH) / 2;
	}

	static int contentRight(int screenWidth) {
		return contentLeft(screenWidth) + CONTENT_WIDTH;
	}

	static int previewButtonLeft(int screenWidth) {
		return contentLeft(screenWidth);
	}

	static int doneButtonLeft(int screenWidth) {
		return previewButtonLeft(screenWidth) + BUTTON_WIDTH + BUTTON_GAP;
	}

	static int settingsButtonLeft(int screenWidth) {
		return doneButtonLeft(screenWidth);
	}

	static int settingsButtonTop(int screenHeight) {
		return screenHeight - 60;
	}

	static int selectedLabelTop(int screenHeight) {
		return screenHeight - 76;
	}

	static int buttonTop(int screenHeight) {
		return screenHeight - 28;
	}

	static int listBottom(int screenHeight) {
		return screenHeight - LIST_BOTTOM_MARGIN;
	}
}
