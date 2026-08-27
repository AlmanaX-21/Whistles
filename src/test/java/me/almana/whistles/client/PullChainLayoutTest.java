package me.almana.whistles.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PullChainLayoutTest {

	@Test
	void keepsTravelWithinTheUpperThird() {
		assertEquals(60, PullChainLayout.travel(180));
		assertEquals(80, PullChainLayout.travel(240));
		assertEquals(96, PullChainLayout.travel(360));
	}

	@Test
	void keepsPulleyGroupsAlignedToTheRight() {
		assertEquals(228, PullChainLayout.controlsLeft(320, 1));
		assertEquals(168, PullChainLayout.controlsLeft(320, 2));
		assertEquals(108, PullChainLayout.controlsLeft(320, 3));
	}

	@Test
	void clampsDraggedPulleyPositionToItsTravel() {
		assertEquals(0, PullChainLayout.pullForTop(-12, 60));
		assertEquals(0.5f, PullChainLayout.pullForTop(30, 60));
		assertEquals(1, PullChainLayout.pullForTop(90, 60));
	}

	@Test
	void acceptsClicksOnlyAroundTheMovingHandle() {
		assertTrue(PullChainLayout.isOverHandle(16, 24, 0, 0, 0, 60));
		assertFalse(PullChainLayout.isOverHandle(16, 10, 0, 0, 0, 60));
		assertTrue(PullChainLayout.isOverHandle(16, 54, 0, 0, 0.5f, 60));
		assertFalse(PullChainLayout.isOverHandle(33, 54, 0, 0, 0.5f, 60));
	}

	@Test
	void preservesTheGrabPointWhileDragging() {
		assertEquals(0.5f, PullChainLayout.pullForMouse(55, 25, 0, 60));
		assertEquals(1, PullChainLayout.pullForMouse(85, 25, 0, 60));
	}

	@Test
	void keepsRopeSegmentsOnTheBraidPeriod() {
		assertEquals(12, PullChainLayout.ropeSegmentHeight(20));
		assertEquals(7, PullChainLayout.ropeSegmentHeight(7));
	}

}
