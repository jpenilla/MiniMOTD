enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/") {
      mavenContent { releasesOnly() }
    }
    maven("https://repo.jpenilla.xyz/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
  }
  includeBuild("gradle/build-logic")
}

plugins {
  id("net.neoforged.moddev.repositories") version "2.0.113" apply false
  id("quiet-fabric-loom") version "1.11-SNAPSHOT" apply false
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "MiniMOTD"

fun setup(name: String, dir: String) {
  include(name)
  project(":$name").projectDir = file(dir)
}

fun platform(name: String) = setup("minimotd-$name", "platform/$name")

setup("minimotd-common", "common")

sequenceOf(
  "paper",
  "bukkit",
  "sponge8",
  "sponge7",
  "bungeecord",
  "velocity",
  "fabric",
  "neoforge",
).forEach(::platform)
