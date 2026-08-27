package me.almana.whistles;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;

class ModMetadataTest {

	@Test
	void acceptsTheSupportedCreateVersions() throws Exception {
		var resource = ModMetadataTest.class.getResourceAsStream("/META-INF/neoforge.mods.toml");
		assertNotNull(resource);
		try (var reader = new InputStreamReader(resource, UTF_8)) {
			var metadata = new TomlParser().parse(reader);
			List<Config> dependencies = metadata.get("dependencies.whistles");
			var createRange = dependencies.stream()
				.filter(dependency -> "create".equals(dependency.get("modId")))
				.map(dependency -> dependency.<String>get("versionRange"))
				.findFirst()
				.orElseThrow();
			var versions = VersionRange.createFromVersionSpec(createRange);

			assertTrue(versions.containsVersion(new DefaultArtifactVersion("6.0.10")));
			assertTrue(versions.containsVersion(new DefaultArtifactVersion("6.0.11")));
			assertFalse(versions.containsVersion(new DefaultArtifactVersion("6.0.9")));
			assertFalse(versions.containsVersion(new DefaultArtifactVersion("6.1.0")));
		}
	}

	@Test
	void requiresTheTargetNeoForgeVersion() throws Exception {
		var resource = ModMetadataTest.class.getResourceAsStream("/META-INF/neoforge.mods.toml");
		assertNotNull(resource);
		try (var reader = new InputStreamReader(resource, UTF_8)) {
			var metadata = new TomlParser().parse(reader);
			List<Config> dependencies = metadata.get("dependencies.whistles");
			var neoForgeRange = dependencies.stream()
				.filter(dependency -> "neoforge".equals(dependency.get("modId")))
				.map(dependency -> dependency.<String>get("versionRange"))
				.findFirst()
				.orElseThrow();
			var versions = VersionRange.createFromVersionSpec(neoForgeRange);

			assertTrue(versions.containsVersion(new DefaultArtifactVersion("21.1.233")));
			assertFalse(versions.containsVersion(new DefaultArtifactVersion("21.1.232")));
			assertFalse(versions.containsVersion(new DefaultArtifactVersion("21.1.234")));
		}
	}
}
