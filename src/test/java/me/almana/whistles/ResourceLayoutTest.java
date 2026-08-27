package me.almana.whistles;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonParser;

class ResourceLayoutTest {

	@Test
	void loadsTheRecipeFromTheMinecraft121Path() throws Exception {
		var resource = ResourceLayoutTest.class.getResourceAsStream(
			"/data/whistles/recipe/train_sound_post.json");
		assertNotNull(resource);
		try (var reader = new InputStreamReader(resource, UTF_8)) {
			var result = JsonParser.parseReader(reader)
				.getAsJsonObject()
				.getAsJsonObject("result");

			assertEquals("whistles:train_sound_post", result.get("id").getAsString());
		}
	}

	@Test
	void loadsTheLootTableFromTheMinecraft121Path() {
		assertNotNull(ResourceLayoutTest.class.getResource(
			"/data/whistles/loot_table/blocks/train_sound_post.json"));
	}
}
