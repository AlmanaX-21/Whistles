package me.almana.whistles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import me.almana.whistles.sound.PitchCodec;

class PitchCodecTest {

	@Test
	void roundTripsWithinAQuarterSemitone() {
		for (float semitones = -12; semitones <= 12; semitones += .37f) {
			float decoded = PitchCodec.decode(PitchCodec.encode(semitones, 12), 12);
			assertTrue(Math.abs(decoded - semitones) < .25f,
				"semitones " + semitones + " round-tripped to " + decoded);
		}
	}

	@Test
	void endpointsAreExact() {
		assertEquals(-12f, PitchCodec.decode(PitchCodec.encode(-12, 12), 12), .01f);
		assertEquals(12f, PitchCodec.decode(PitchCodec.encode(12, 12), 12), .01f);
		assertEquals(0f, PitchCodec.decode(PitchCodec.encode(0, 12), 12), .05f);
	}

	@Test
	void clampsBeyondTheRange() {
		assertEquals(5f, PitchCodec.clampSemitones(40, 5));
		assertEquals(-5f, PitchCodec.clampSemitones(-40, 5));
		assertEquals(12f, PitchCodec.clampSemitones(40, 99), "range is capped at one octave");
	}

	@Test
	void playbackPitchStaysInsideTheEngineClamp() {
		assertEquals(1f, PitchCodec.playbackPitch(0), .001f);
		assertEquals(2f, PitchCodec.playbackPitch(12), .001f);
		assertEquals(.5f, PitchCodec.playbackPitch(-12), .001f);
		assertEquals(2f, PitchCodec.playbackPitch(48), .001f);
		assertEquals(.5f, PitchCodec.playbackPitch(-48), .001f);
	}
}
