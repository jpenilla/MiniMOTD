enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
  repositories {
    mavenCentral {
      mavenContent { releasesOnly() }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://repo.jpenilla.xyz/snapshots/")
  }
  includeBuild("gradle/build-logic")
}

plugins {
  id("quiet-fabric-loom") version "1.6-SNAPSHOT"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "MiniMOTD"

fun setup(name: String, dir: String) {
  include(name)
  project(":$name").projectDir = file(dir)
}

fun platform(name: String) = setup("minimotd-$name", "platform/$name")
fun dist(name: String) = setup("minimotd-$name", "dist/$name")

setup("minimotd-common", "common")

sequenceOf(
  "bukkit",
  "sponge8",
  "sponge7",
  "bungeecord",
  "velocity",
  "fabric",
).forEach(::platform)

dist("bukkit-bungeecord")
