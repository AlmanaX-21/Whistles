package me.almana.whistles.client;

final class PullChainLayout {

	static final int PULLEY_WIDTH = 32;
	static final int PULLEY_HEIGHT = 32;
	static final int MAX_TRAVEL = 96;
	static final int GAP = 28;
	static final int RIGHT_MARGIN = 60;
	static final int LABEL_GAP = 6;
	private static final int HANDLE_TOP = 18;
	private static final int HANDLE_HEIGHT = 16;
	private static final int ROPE_REPEAT_HEIGHT = 12;

	private PullChainLayout() {
	}

	static int travel(int screenHeight) {
		return Math.min(screenHeight / 3, MAX_TRAVEL);
	}

	static int controlsLeft(int screenWidth, int count) {
		int totalWidth = count * PULLEY_WIDTH + Math.max(0, count - 1) * GAP;
		return screenWidth - RIGHT_MARGIN - totalWidth;
	}

	static float pullForTop(double top, int travel) {
		return (float) Math.max(0, Math.min(1, top / travel));
	}

	static boolean isOverHandle(double mouseX, double mouseY, int x, int y, float pull, int travel) {
		int handleTop = y + Math.round(pull * travel) + HANDLE_TOP;
		return mouseX >= x && mouseX < x + PULLEY_WIDTH
			&& mouseY >= handleTop && mouseY < handleTop + HANDLE_HEIGHT;
	}

	static float pullForMouse(double mouseY, double grabOffset, int y, int travel) {
		return pullForTop(mouseY - grabOffset - y, travel);
	}

	static int ropeSegmentHeight(int remaining) {
		return Math.min(remaining, ROPE_REPEAT_HEIGHT);
	}
}
