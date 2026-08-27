package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrainSoundSelectLayoutTest {

	@Test
	void alignsControlsOnOneCenteredColumn() {
		int screenWidth = 320;
		int left = TrainSoundSelectLayout.contentLeft(screenWidth);
		int right = TrainSoundSelectLayout.contentRight(screenWidth);

		assertEquals(60, left);
		assertEquals(260, right);
		assertEquals(left, TrainSoundSelectLayout.previewButtonLeft(screenWidth));
		assertEquals(TrainSoundSelectLayout.doneButtonLeft(screenWidth),
			TrainSoundSelectLayout.settingsButtonLeft(screenWidth));
		assertEquals(right,
			TrainSoundSelectLayout.doneButtonLeft(screenWidth) + TrainSoundSelectLayout.BUTTON_WIDTH);
		assertEquals(4,
			TrainSoundSelectLayout.doneButtonLeft(screenWidth)
				- TrainSoundSelectLayout.previewButtonLeft(screenWidth)
				- TrainSoundSelectLayout.BUTTON_WIDTH);
	}

	@Test
	void keepsTheSettingsRowBetweenTheListAndActionButtons() {
		int screenHeight = 240;

		assertTrue(TrainSoundSelectLayout.listBottom(screenHeight)
			< TrainSoundSelectLayout.settingsButtonTop(screenHeight));
		assertTrue(TrainSoundSelectLayout.settingsButtonTop(screenHeight) + TrainSoundSelectLayout.BUTTON_HEIGHT
			< TrainSoundSelectLayout.buttonTop(screenHeight));
	}

	@Test
	void keepsBothTextLinesInsideSelectionOutline() {
		int fontHeight = 9;
		int selectionTop = -2;
		int selectionBottom = TrainSoundSelectLayout.ROW_HEIGHT - 2;
		int nameBottom = TrainSoundSelectLayout.NAME_TOP + fontHeight;
		int identifierBottom = TrainSoundSelectLayout.IDENTIFIER_TOP + fontHeight;

		assertTrue(TrainSoundSelectLayout.NAME_TOP > selectionTop);
		assertTrue(TrainSoundSelectLayout.IDENTIFIER_TOP > nameBottom);
		assertTrue(identifierBottom < selectionBottom);
	}
}
