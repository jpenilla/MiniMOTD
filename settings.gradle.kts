enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://repo.stellardrift.ca/repository/snapshots/")
  }
}

plugins {
  id("ca.stellardrift.polyglot-version-catalogs") version "5.0.0-SNAPSHOT"
}

rootProject.name = "MiniMOTD"

includeBuild("build-logic")

setupSubproject("minimotd-common") {
  projectDir = file("common")
}
setupSubproject("minimotd-spigot") {
  projectDir = file("spigot")
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
