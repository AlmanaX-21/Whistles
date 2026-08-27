package me.almana.whistles;

import me.almana.whistles.sound.PitchCodec;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

	private static final ForgeConfigSpec.IntValue PITCH_RANGE;
	private static final ForgeConfigSpec.DoubleValue VOLUME;
	private static final ForgeConfigSpec.IntValue HEARING_RANGE;
	private static final ForgeConfigSpec.DoubleValue LEVER_VOLUME_INFLUENCE;
	private static final ForgeConfigSpec.DoubleValue LEVER_VOLUME_MIN;
	private static final ForgeConfigSpec.DoubleValue LEVER_VOLUME_MAX;
	private static final ForgeConfigSpec SERVER_SPEC;

	private static final ForgeConfigSpec.IntValue HANG_TICKS;
	private static final ForgeConfigSpec CLIENT_SPEC;

	static {
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		PITCH_RANGE = server
			.comment("Default pitch bend copied to newly placed train sound posts, in semitones.",
				"The sound engine hard-clamps playback to one octave, so 12 is the maximum.")
			.defineInRange("pitchRangeSemitones", PitchCodec.MAX_RANGE, 1, PitchCodec.MAX_RANGE);
		VOLUME = server.comment("Default volume copied to newly placed train sound posts.")
			.defineInRange("volume", 1.0, 0.0, 1.0);
		HEARING_RANGE = server.comment("Default hearing range copied to newly placed train sound posts, in blocks.")
			.defineInRange("hearingRange", 64, 8, 128);
		LEVER_VOLUME_INFLUENCE = server.comment("Default lever influence copied to newly placed train sound posts.",
				"1.0 is linear; higher reaches max volume before the lever is fully down; lower keeps volume low until nearly fully down.")
			.defineInRange("leverVolumeInfluence", 1.0, 0.1, 5.0);
		LEVER_VOLUME_MIN = server.comment("Default topmost lever volume copied to newly placed train sound posts, in percent.")
			.defineInRange("leverVolumeMin", 0.0, 0.0, 100.0);
		LEVER_VOLUME_MAX = server.comment("Default fully pulled volume copied to newly placed train sound posts, in percent.")
			.defineInRange("leverVolumeMax", 100.0, 0.0, 100.0);
		SERVER_SPEC = server.build();

		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		HANG_TICKS = client.comment("Ticks the pull chain hangs at its pulled position after release before easing back up.")
			.defineInRange("hangTicks", 20, 0, 200);
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

	public static boolean serverConfigEditable() {
		return SERVER_SPEC.isLoaded();
	}

	public static float volume() {
		return SERVER_SPEC.isLoaded() ? VOLUME.get()
			.floatValue() : 1f;
	}

	public static void setVolume(double volume) {
		VOLUME.set(volume);
		VOLUME.save();
	}

	public static float hearingRange() {
		return SERVER_SPEC.isLoaded() ? HEARING_RANGE.get() : 64;
	}

	public static void setHearingRange(int blocks) {
		HEARING_RANGE.set(blocks);
		HEARING_RANGE.save();
	}

	public static float leverVolumeInfluence() {
		return SERVER_SPEC.isLoaded() ? LEVER_VOLUME_INFLUENCE.get()
			.floatValue() : 1f;
	}

	public static void setLeverVolumeInfluence(double influence) {
		LEVER_VOLUME_INFLUENCE.set(influence);
		LEVER_VOLUME_INFLUENCE.save();
	}

	public static float leverVolumeMin() {
		return SERVER_SPEC.isLoaded() ? LEVER_VOLUME_MIN.get()
			.floatValue() : 0f;
	}

	public static void setLeverVolumeMin(double percent) {
		LEVER_VOLUME_MIN.set(percent);
		LEVER_VOLUME_MIN.save();
	}

	public static float leverVolumeMax() {
		return SERVER_SPEC.isLoaded() ? LEVER_VOLUME_MAX.get()
			.floatValue() : 100f;
	}

	public static void setLeverVolumeMax(double percent) {
		LEVER_VOLUME_MAX.set(percent);
		LEVER_VOLUME_MAX.save();
	}

	public static int hangTicks() {
		return CLIENT_SPEC.isLoaded() ? HANG_TICKS.get() : 20;
	}

	public static void setHangTicks(int ticks) {
		HANG_TICKS.set(ticks);
		HANG_TICKS.save();
	}
}
