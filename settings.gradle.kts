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
    maven("https://jitpack.io") {
      content { includeGroupByRegex("com\\.github\\..*") }
    }
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

setupSubproject("minimotd-common") {
  projectDir = file("common")
}
setupSubproject("minimotd-bukkit") {
  projectDir = file("bukkit")
}
setupSubproject("minimotd-sponge8") {
  projectDir = file("sponge8")
}
setupSubproject("minimotd-sponge7") {
  projectDir = file("sponge7")
}
setupSubproject("minimotd-bungeecord") {
  projectDir = file("bungee")
}
setupSubproject("minimotd-velocity") {
  projectDir = file("velocity")
}
setupSubproject("minimotd-fabric") {
  projectDir = file("fabric")
}
setupSubproject("minimotd-universal") {
  projectDir = file("universal")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
  include(name)
  project(":$name").apply(block)
}
