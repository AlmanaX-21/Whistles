package me.almana.whistles.sound;

public class VolumeCodec {

	public static float leverVolume(float pull, float influence, float minVolume, float maxVolume) {
		float effectivePull = Math.max(0, Math.min(1, pull * influence));
		float volume = minVolume + (maxVolume - minVolume) * effectivePull;
		return Math.max(0, Math.min(100, volume));
	}
}
