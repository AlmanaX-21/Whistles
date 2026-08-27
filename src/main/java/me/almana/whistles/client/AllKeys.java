package me.almana.whistles.client;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

public class AllKeys {

	public static final String CATEGORY = "key.categories.whistles";

	public static final KeyMapping OPEN_CONTROLS = new KeyMapping("key.whistles.open_controls", GLFW.GLFW_KEY_H, CATEGORY);

	public static KeyMapping[] all() {
		return new KeyMapping[] { OPEN_CONTROLS };
	}
}
