plugins {
    java
    application
    signing
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.dawciobiel"
version = "3.2.2"

description =
"A Java library for building interactive console dialogs, menus, and wizards."

// Java configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    // Usunięto withSourcesJar() i withJavadocJar() - plugin vanniktech zrobi to automatycznie
}

application {
    mainClass = "io.github.dawciobiel.shelldialog.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version.toString()
    }
}

val fatJar by tasks.registering(Jar::class) {
    group = "build"
    description = "Builds an executable fat JAR with all runtime dependencies."
    archiveClassifier = "all"

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version.toString()
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.assemble {
    dependsOn(fatJar)
}

tasks.test {
    useJUnitPlatform()
    forkEvery = 1
        maxParallelForks = 1
}

/*
 * Maven Central (Central Portal 2025)
 */
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = "io.github.dawciobiel",
        artifactId = "shelldialog",
        version = version.toString()
    )

    pom {
        name.set("shelldialog")
        description.set(project.description)
        url.set("https://github.com/dawciobiel/shelldialog")
        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
            }
        }
        developers {
            developer {
                id.set("dawciobiel")
                name.set("Dawid Bielecki")
                email.set("3913996+dawciobiel@users.noreply.github.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/dawciobiel/shelldialog.git")
            developerConnection.set("scm:git:ssh://github.com/dawciobiel/shelldialog.git")
            url.set("https://github.com/dawciobiel/shelldialog")
        }
    }
}

// Użycie systemowego GPG (skonfigurowane w gradle.properties)
signing {
    useGpgCmd()
}
