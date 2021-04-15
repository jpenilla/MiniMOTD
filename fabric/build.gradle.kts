plugins {
  id("fabric-loom") version "0.7-SNAPSHOT"
}

val shade: Configuration by configurations.creating

val minecraftVersion = libs.versions.fabricMinecraft.get()

dependencies {
  minecraft(libs.fabricMinecraft)
  mappings(minecraft.officialMojangMappings())
  modImplementation(libs.fabricLoader)
  modImplementation(libs.fabricApi)

  shade(implementation(projects.minimotdCommon) {
    exclude("net.kyori")
    exclude("org.slf4j")
  })

  modImplementation(libs.adventurePlatformFabric)
  include(libs.adventurePlatformFabric)
  implementation(libs.minimessage)
  include(libs.minimessage)

  implementation(libs.slf4jApi)
  include(libs.slf4jApi)
  implementation(libs.slf4jLog4jImpl)
  include(libs.slf4jLog4jImpl)
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
    input.set(shadowJar.get().archiveFile)
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
