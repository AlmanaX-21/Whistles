package me.almana.whistles.client;

import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class TrainArrivalSoundInstance extends AbstractSoundInstance {

	public TrainArrivalSoundInstance(ResourceLocation sound, TrainSoundSettings settings, Vec3 location) {
		super(SoundEvent.createFixedRangeEvent(sound, settings.hearingRange()), SoundSource.NEUTRAL,
			SoundInstance.createUnseededRandom());
		attenuation = Attenuation.LINEAR;
		looping = false;
		delay = 0;
		volume = settings.volume();
		pitch = 1;
		x = location.x;
		y = location.y;
		z = location.z;
	}
}
