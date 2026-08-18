package me.almana.whistles.sound;

public class SoundIds {

	public static final String PREFIX = "train_sound/";

	public static boolean isTrainSound(String path) {
		return path.startsWith(PREFIX) && path.length() > PREFIX.length();
	}

	public static String displayName(String path) {
		String name = path.startsWith(PREFIX) ? path.substring(PREFIX.length()) : path;
		StringBuilder out = new StringBuilder();
		for (String word : name.replace('/', ' ').replace('_', ' ').trim().split("\\s+")) {
			if (word.isEmpty())
				continue;
			if (out.length() > 0)
				out.append(' ');
			out.append(Character.toUpperCase(word.charAt(0)))
				.append(word.substring(1));
		}
		return out.toString();
	}
}
