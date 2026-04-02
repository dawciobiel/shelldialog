package io.github.dawciobiel.shelldialog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionConsistencyTest {

    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();
    private static final Pattern GRADLE_VERSION = Pattern.compile("^version\\s*=\\s*\"([^\"]+)\"", Pattern.MULTILINE);
    private static final Pattern README_CURRENT_VERSION = Pattern.compile("\\*\\*Current version: ([^*]+)\\*\\*");
    private static final Pattern CHANGELOG_RELEASE = Pattern.compile("^## \\[([^\\]]+)] - ", Pattern.MULTILINE);

    @Test
    void releaseMetadataShouldUseTheSameVersionAcrossProjectFiles() throws IOException {
        String gradleVersion = capture(GRADLE_VERSION, read("build.gradle.kts"));
        String readmeVersion = capture(README_CURRENT_VERSION, read("README.md"));
        String changelogVersion = capture(CHANGELOG_RELEASE, read("CHANGELOG.md"));

        assertEquals(gradleVersion, readmeVersion, "README current version should match the build version");
        assertEquals(gradleVersion, changelogVersion, "Latest changelog release should match the build version");
    }

    @Test
    void readmeUsageSnippetsShouldReferenceCurrentBuildVersion() throws IOException {
        String buildVersion = capture(GRADLE_VERSION, read("build.gradle.kts"));
        String readme = read("README.md");

        assertTrue(readme.contains("io.github.dawciobiel:shelldialog:" + buildVersion));
        assertTrue(readme.contains("<version>" + buildVersion + "</version>"));
        assertTrue(readme.contains("/shelldialog/" + buildVersion + "/"));
        assertTrue(readme.contains("shelldialog-" + buildVersion + "-all.jar"));
    }

    private String read(String relativePath) throws IOException {
        return Files.readString(PROJECT_ROOT.resolve(relativePath));
    }

    private String capture(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find(), "Pattern not found: " + pattern);
        return matcher.group(1).trim();
    }
}
