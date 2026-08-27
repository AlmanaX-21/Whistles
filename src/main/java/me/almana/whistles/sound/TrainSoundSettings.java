package me.almana.whistles.sound;

import me.almana.whistles.Config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record TrainSoundSettings(int pitchRange, float volume, int hearingRange, float leverVolumeInfluence,
	float leverVolumeMin, float leverVolumeMax) {

	public static TrainSoundSettings fromConfig() {
		return new TrainSoundSettings(Config.pitchRange(), Config.volume(), (int) Config.hearingRange(),
			Config.leverVolumeInfluence(), Config.leverVolumeMin(), Config.leverVolumeMax());
	}

	public boolean valid() {
		return pitchRange >= 1 && pitchRange <= PitchCodec.MAX_RANGE
			&& Float.isFinite(volume) && volume >= 0 && volume <= 1
			&& hearingRange >= 8 && hearingRange <= 128
			&& Float.isFinite(leverVolumeInfluence) && leverVolumeInfluence >= .1f && leverVolumeInfluence <= 5
			&& Float.isFinite(leverVolumeMin) && leverVolumeMin >= 0 && leverVolumeMin <= 100
			&& Float.isFinite(leverVolumeMax) && leverVolumeMax >= 0 && leverVolumeMax <= 100;
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("PitchRange", pitchRange);
		tag.putFloat("Volume", volume);
		tag.putInt("HearingRange", hearingRange);
		tag.putFloat("LeverVolumeInfluence", leverVolumeInfluence);
		tag.putFloat("LeverVolumeMin", leverVolumeMin);
		tag.putFloat("LeverVolumeMax", leverVolumeMax);
		return tag;
	}

	public static TrainSoundSettings read(CompoundTag tag) {
		return new TrainSoundSettings(tag.getInt("PitchRange"), tag.getFloat("Volume"), tag.getInt("HearingRange"),
			tag.getFloat("LeverVolumeInfluence"), tag.getFloat("LeverVolumeMin"), tag.getFloat("LeverVolumeMax"));
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(pitchRange);
		buffer.writeFloat(volume);
		buffer.writeVarInt(hearingRange);
		buffer.writeFloat(leverVolumeInfluence);
		buffer.writeFloat(leverVolumeMin);
		buffer.writeFloat(leverVolumeMax);
	}

	public static TrainSoundSettings read(FriendlyByteBuf buffer) {
		return new TrainSoundSettings(buffer.readVarInt(), buffer.readFloat(), buffer.readVarInt(), buffer.readFloat(),
			buffer.readFloat(), buffer.readFloat());
	}
}
