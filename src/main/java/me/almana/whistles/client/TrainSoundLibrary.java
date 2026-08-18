package me.almana.whistles.client;

import java.util.Comparator;
import java.util.List;

import me.almana.whistles.sound.SoundIds;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class TrainSoundLibrary {

	public static List<ResourceLocation> available() {
		return Minecraft.getInstance()
			.getSoundManager()
			.getAvailableSounds()
			.stream()
			.filter(id -> SoundIds.isTrainSound(id.getPath()))
			.sorted(Comparator.comparing(ResourceLocation::toString))
			.toList();
	}
}
