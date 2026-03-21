plugins {
    java
    application
    `maven-publish`
}

group = "org.dawciobiel"
version = "2.1.0"

description =
    "A Java library for building interactive console dialogs, menus, and wizards."

// Java configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.dawciobiel.shelldialog.Main"
}

// Repositories
repositories {
    mavenCentral()
}

// Dependencies
dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.3")
}

// Compilation configuration
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Fat JAR (equivalent to maven-shade-plugin)
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Tests (equivalent to maven-surefire-plugin)
tasks.test {
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
