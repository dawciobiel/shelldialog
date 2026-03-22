# Publishing

This project is prepared for publication to Maven Central.

## Published coordinates

The current published artifact coordinates are:

- `io.github.dawciobiel:shelldialog:3.0.0`

Useful links:

- `https://central.sonatype.com/artifact/io.github.dawciobiel/shelldialog`
- `https://repo1.maven.org/maven2/io/github/dawciobiel/shelldialog/3.0.0/`

## Artifact layout

- the standard `jar` is the library artifact intended for dependency consumption
- the executable fat JAR is built separately as `shelldialog-<version>-all.jar`

## Maven Central prerequisites

Before the first publication, make sure you have:

- a Sonatype Central Portal account
- a verified namespace matching the project `groupId`
- a GPG key for artifact signing
- a Central publishing token configured in Maven settings

Important:

- the current `groupId` is `io.github.dawciobiel`
- publishing with this `groupId` requires ownership of that namespace
- `io.github.<github-username>` is the usual Maven Central namespace for projects published from a GitHub identity

## Maven settings

Configure credentials in `~/.m2/settings.xml` under server id `central`.

Example:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>${env.CENTRAL_USERNAME}</username>
      <password>${env.CENTRAL_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

## Release publication

To publish a release to Maven Central:

```bash
mvn clean deploy
```

The Maven build is configured to:

- attach sources and Javadoc JARs
- sign artifacts with GPG during `verify`
- publish through the Sonatype Central publishing plugin

## Gradle usage

Gradle remains the primary local build tool for development:

```bash
./gradlew clean test jar fatJar
```

Use `fatJar` when you want the executable JAR for local CLI runs.
