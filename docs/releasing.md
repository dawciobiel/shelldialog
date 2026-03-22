# Releasing

This document is a practical checklist for shipping a new ShellDialog release.

## Before you start

Make sure you already have:

- a verified Sonatype Central namespace
- a valid Central user token in `~/.m2/settings.xml` under server id `central`
- a working local GPG key for artifact signing

## Release checklist

1. Choose the new version number.
2. Update the version in:
   - `build.gradle.kts`
   - `pom.xml`
   - `README.md`
   - `CHANGELOG.md`
3. Review public documentation:
   - `README.md`
   - `docs/`
   - Javadoc of any changed public API
4. Verify the Gradle build:

```bash
./gradlew clean test jar fatJar
```

5. Verify the Maven build:

```bash
mvn clean deploy
```

6. Open Sonatype Central Portal and wait for the deployment status to become `PUBLISHED`.
7. Push the release commit if it has not been pushed yet.
8. Create the Git tag for the release.
9. Push the tag:

```bash
git push origin <tag>
```

10. Create the GitHub Release and publish release notes.
11. Verify public artifact availability:
   - Sonatype Central search page
   - Maven Central repository URL

## Post-release checks

- confirm that `java -jar build/libs/shelldialog-<version>-all.jar --version` prints the released version
- confirm that dependency coordinates in `README.md` match the published version
- confirm that `CHANGELOG.md` reflects the release accurately
