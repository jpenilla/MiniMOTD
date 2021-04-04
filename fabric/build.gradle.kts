plugins {
  id("fabric-loom") version "0.6-SNAPSHOT"
}

val shade: Configuration by configurations.creating

val minecraftVersion = "1.16.5"

dependencies {
  minecraft("com.mojang", "minecraft", minecraftVersion)
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc", "fabric-loader", "0.11.1")
  modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.31.0+1.16")

  shade(implementation(project(":minimotd-common")) {
    exclude("net.kyori")
    exclude("org.slf4j")
  })

  modImplementation(include("net.kyori", "adventure-platform-fabric", "4.0.0-SNAPSHOT"))
  implementation(include("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT"))

  implementation(include("org.slf4j", "slf4j-api", "1.7.30"))
  implementation(include("org.apache.logging.log4j", "log4j-slf4j-impl", "2.8.1"))
}

tasks {
  shadowJar {
    configurations = listOf(shade)
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.lib.fabric.minimotd.common")
  }
  remapJar {
    dependsOn(shadowJar)
    input.set(shadowJar.get().outputs.files.singleFile)
    archiveFileName.set("${project.name}-mc$minecraftVersion-${project.version}.jar")
    destinationDirectory.set(rootProject.buildDir.resolve("libs"))
  }
  processResources {
    filesMatching("fabric.mod.json") {
      mapOf(
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{project.version}" to project.version.toString(),
        "{project.description}" to project.description.toString(),
        "{project.github_url}" to rootProject.ext["url"].toString()
      ).forEach { (k, v) ->
        filter { it.replace(k, v) }
      }
    }
  }
}
