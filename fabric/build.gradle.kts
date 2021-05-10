plugins {
  id("fabric-loom")
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
  implementation(libs.log4jSlf4jImpl)
  include(libs.log4jSlf4jImpl)
}

tasks {
  shadowJar {
    configurations = listOf(shade)
    commonRelocation("io.leangen.geantyref")
    platformRelocation("fabric", "xyz.jpenilla.minimotd.common")
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
        "{project.github_url}" to Constants.GITHUB_URL
      ).forEach { (k, v) ->
        filter { it.replace(k, v) }
      }
    }
  }
}
