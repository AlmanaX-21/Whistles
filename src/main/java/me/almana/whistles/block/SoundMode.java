package me.almana.whistles.block;

import net.minecraft.util.StringRepresentable;

public enum SoundMode implements StringRepresentable {

	WHISTLE,
	HORN;

	public SoundMode next() {
		return this == WHISTLE ? HORN : WHISTLE;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
