enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
  repositories {
    //mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/")
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
  }
  includeBuild("build-logic")
}

plugins {
  id("ca.stellardrift.polyglot-version-catalogs") version "5.0.0"
  id("fabric-loom") version "0.8-SNAPSHOT"
}

rootProject.name = "MiniMOTD"

sequenceOf(
  "common",
  "bukkit",
  "sponge8",
  "sponge7",
  "bungeecord",
  "velocity",
  "fabric",
  "universal"
).forEach {
  include("minimotd-$it")
  project(":minimotd-$it").projectDir = file(it)
}
