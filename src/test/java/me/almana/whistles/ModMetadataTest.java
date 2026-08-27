package me.almana.whistles;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;

class ModMetadataTest {

	@Test
	void acceptsPublicCreateSixVersions() throws Exception {
		try (var reader = new InputStreamReader(Objects.requireNonNull(
			ModMetadataTest.class.getResourceAsStream("/META-INF/mods.toml")), UTF_8)) {
			var metadata = new TomlParser().parse(reader);
			List<Config> dependencies = metadata.get("dependencies.whistles");
			var createRange = dependencies.stream()
				.filter(dependency -> "create".equals(dependency.get("modId")))
				.map(dependency -> dependency.<String>get("versionRange"))
				.findFirst()
				.orElseThrow();
			var versions = VersionRange.createFromVersionSpec(createRange);

			assertTrue(versions.containsVersion(new DefaultArtifactVersion("6.0.0")));
			assertTrue(versions.containsVersion(new DefaultArtifactVersion("6.0.8")));
			assertFalse(versions.containsVersion(new DefaultArtifactVersion("0.5.1")));
		}
	}
}
