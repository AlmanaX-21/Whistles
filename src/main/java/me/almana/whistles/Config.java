package me.almana.whistles;

import me.almana.whistles.sound.PitchCodec;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

	private static final ForgeConfigSpec.IntValue PITCH_RANGE;
	private static final ForgeConfigSpec.DoubleValue VOLUME;
	private static final ForgeConfigSpec.IntValue HEARING_RANGE;
	private static final ForgeConfigSpec SERVER_SPEC;

	private static final ForgeConfigSpec.IntValue SWEEP_TICKS;
	private static final ForgeConfigSpec CLIENT_SPEC;

	static {
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		PITCH_RANGE = server
			.comment("How far the pitch can bend either way, in semitones.",
				"The sound engine hard-clamps playback to one octave, so 12 is the maximum.")
			.defineInRange("pitchRangeSemitones", PitchCodec.MAX_RANGE, 1, PitchCodec.MAX_RANGE);
		VOLUME = server.comment("Master volume for train horns and whistles.")
			.defineInRange("volume", 1.0, 0.0, 1.0);
		HEARING_RANGE = server.comment("How far a horn or whistle carries, in blocks.")
			.defineInRange("hearingRange", 64, 8, 128);
		SERVER_SPEC = server.build();

		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		SWEEP_TICKS = client.comment("Ticks to sweep the pitch from centre to either end of the range.")
			.defineInRange("sweepTicks", 40, 5, 400);
		CLIENT_SPEC = client.build();
	}

	public static void register() {
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
	}

	public static int pitchRange() {
		return SERVER_SPEC.isLoaded() ? PITCH_RANGE.get() : PitchCodec.MAX_RANGE;
	}

	public static void setPitchRange(int semitones) {
		PITCH_RANGE.set(semitones);
		PITCH_RANGE.save();
	}

	public static boolean pitchRangeEditable() {
		return SERVER_SPEC.isLoaded();
	}

	public static float volume() {
		return SERVER_SPEC.isLoaded() ? VOLUME.get()
			.floatValue() : 1f;
	}

	public static float hearingRange() {
		return SERVER_SPEC.isLoaded() ? HEARING_RANGE.get() : 64;
	}

	public static int sweepTicks() {
		return CLIENT_SPEC.isLoaded() ? SWEEP_TICKS.get() : 40;
	}
}
