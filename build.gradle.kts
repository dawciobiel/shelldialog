plugins {
    java
    application
    `maven-publish`
    signing
}

group = "io.github.dawciobiel"
version = "3.0.0"

description =
    "A Java library for building interactive console dialogs, menus, and wizards."

// Java configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

application {
    mainClass = "io.github.dawciobiel.shelldialog.Main"
}

// Repositories
repositories {
    mavenCentral()
}

// Dependencies
dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Compilation configuration
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

// Tests (equivalent to maven-surefire-plugin)
tasks.test {
    useJUnitPlatform()
    forkEvery = 1
    maxParallelForks = 1
}

// Publishing (equivalent to maven-publish)
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "shelldialog"
                description = project.description
                url = "https://github.com/dawciobiel/shelldialog"
                organization {
                    name = "dawciobiel"
                    url = "https://github.com/dawciobiel"
                }
                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                    }
                }
                developers {
                    developer {
                        id = "dawciobiel"
                        name = "Dawid Bielecki"
                        email = "3913996+dawciobiel@users.noreply.github.com"
                        organization = "dawciobiel"
                        organizationUrl = "https://github.com/dawciobiel"
                    }
                }
                scm {
                    connection =
                        "scm:git:git://github.com/dawciobiel/shelldialog.git"
                    developerConnection =
                        "scm:git:ssh://github.com/dawciobiel/shelldialog.git"
                    url = "https://github.com/dawciobiel/shelldialog"
                }
            }
        }
    }
}

val signingKey = providers.gradleProperty("signingKey")
    .orElse(providers.environmentVariable("SIGNING_KEY"))
val signingPassword = providers.gradleProperty("signingPassword")
    .orElse(providers.environmentVariable("SIGNING_PASSWORD"))

signing {
    if (signingKey.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassword.orNull)
        sign(publishing.publications)
    }
}
