package me.almana.whistles.sound;

public class PitchCodec {

	public static final float MIN_PLAYBACK = 0.5f;
	public static final float MAX_PLAYBACK = 2.0f;

	/** The sound engine clamps playback pitch to [0.5, 2.0], which is exactly one octave either way. */
	public static final int MAX_RANGE = 12;

	public static float clampSemitones(float semitones, int range) {
		int limit = Math.min(range, MAX_RANGE);
		return Math.max(-limit, Math.min(limit, semitones));
	}

	public static byte encode(float semitones, int range) {
		int limit = Math.min(range, MAX_RANGE);
		float normalised = (clampSemitones(semitones, limit) + limit) / (2f * limit);
		return (byte) (Math.round(normalised * 255f) - 128);
	}

	public static float decode(byte packed, int range) {
		int limit = Math.min(range, MAX_RANGE);
		return (packed + 128) / 255f * 2f * limit - limit;
	}

	public static float playbackPitch(float semitones) {
		float pitch = (float) Math.pow(2, semitones / 12.0);
		return Math.max(MIN_PLAYBACK, Math.min(MAX_PLAYBACK, pitch));
	}

	public static float normalizedPull(float semitones, int range) {
		int limit = Math.min(range, MAX_RANGE);
		return Math.max(0, Math.min(1, (semitones + limit) / (2f * limit)));
	}
}
