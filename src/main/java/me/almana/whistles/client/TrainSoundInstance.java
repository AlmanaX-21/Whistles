package me.almana.whistles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class TrainSoundInstance extends AbstractTickableSoundInstance {

	public TrainSoundInstance(ResourceLocation sound, float range) {
		super(SoundEvent.createFixedRangeEvent(sound, range), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
		attenuation = Attenuation.LINEAR;
		looping = true;
		delay = 0;
		volume = 0.0001f;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setLocation(Vec3 location) {
		x = location.x;
		y = location.y;
		z = location.z;
	}

	public void stopSound() {
		Minecraft.getInstance()
			.getSoundManager()
			.stop(this);
	}

	@Override
	public void tick() {
	}
}
