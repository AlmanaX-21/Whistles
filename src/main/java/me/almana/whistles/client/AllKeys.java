package me.almana.whistles.client;

import org.lwjgl.glfw.GLFW;

import me.almana.whistles.block.SoundMode;

import net.minecraft.client.KeyMapping;

public class AllKeys {

	public static final String CATEGORY = "key.categories.whistles";

	public static final KeyMapping WHISTLE = new KeyMapping("key.whistles.whistle", GLFW.GLFW_KEY_J, CATEGORY);
	public static final KeyMapping WHISTLE_UP = new KeyMapping("key.whistles.whistle_up", GLFW.GLFW_KEY_UP, CATEGORY);
	public static final KeyMapping WHISTLE_DOWN = new KeyMapping("key.whistles.whistle_down", GLFW.GLFW_KEY_DOWN, CATEGORY);

	public static final KeyMapping HORN = new KeyMapping("key.whistles.horn", GLFW.GLFW_KEY_N, CATEGORY);
	public static final KeyMapping HORN_UP = new KeyMapping("key.whistles.horn_up", GLFW.GLFW_KEY_RIGHT, CATEGORY);
	public static final KeyMapping HORN_DOWN = new KeyMapping("key.whistles.horn_down", GLFW.GLFW_KEY_LEFT, CATEGORY);

	public static final KeyMapping OPEN_CONTROLS = new KeyMapping("key.whistles.open_controls", GLFW.GLFW_KEY_H, CATEGORY);

	public static KeyMapping sound(SoundMode mode) {
		return mode == SoundMode.HORN ? HORN : WHISTLE;
	}

	public static KeyMapping up(SoundMode mode) {
		return mode == SoundMode.HORN ? HORN_UP : WHISTLE_UP;
	}

	public static KeyMapping down(SoundMode mode) {
		return mode == SoundMode.HORN ? HORN_DOWN : WHISTLE_DOWN;
	}

	public static KeyMapping[] all() {
		return new KeyMapping[] { WHISTLE, WHISTLE_UP, WHISTLE_DOWN, HORN, HORN_UP, HORN_DOWN, OPEN_CONTROLS };
	}
}
