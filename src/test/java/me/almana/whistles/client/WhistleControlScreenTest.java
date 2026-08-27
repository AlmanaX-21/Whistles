package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class WhistleControlScreenTest {

	@Test
	void leavesWorldSharpBehindPulleyOverlay() {
		assertDoesNotThrow(() -> new TestScreen().renderPulleyBackground());
	}

	private static class TestScreen extends WhistleControlScreen {

		void renderPulleyBackground() {
			super.renderBlurredBackground(0);
		}
	}
}
