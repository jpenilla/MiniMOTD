enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.architectury.dev/")
    maven("https://repo.jpenilla.xyz/snapshots/")
  }
  includeBuild("gradle/build-logic")
}

plugins {
  id("xyz.jpenilla.quiet-architectury-loom") version "1.7-SNAPSHOT" apply false
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
  "neoforge",
).forEach(::platform)

dist("bukkit-bungeecord")
