plugins {
  id("quiet-fabric-loom")
  id("minimotd.platform-conventions")
  id("com.gradleup.shadow")
}

val shade: Configuration by configurations.creating

dependencies {
  minecraft(libs.minecraft)
  mappings(loom.officialMojangMappings())
  modImplementation(libs.fabricLoader)
  modImplementation(libs.fabricApi)

  shade(implementation(projects.minimotdCommon) {
    exclude("net.kyori")
    exclude("com.google.errorprone")
  })

  modImplementation(libs.adventurePlatformFabric)
  include(libs.adventurePlatformFabric)
}

miniMOTDPlatform {
  jarTask.set(tasks.remapJar)
}

indra {
  javaVersions {
    target(21)
  }
}

tasks {
  shadowJar {
    configurations = listOf(shade)
    commonConfiguration()
    commonRelocation("io.leangen.geantyref")
  }
  remapJar {
    archiveFileName.set("${project.name}-mc$minecraftVersion-${project.version}.jar")
  }
  processResources {
    val replacements = mapOf(
      "modid" to project.name,
      "name" to rootProject.name,
      "version" to project.version.toString(),
      "description" to project.description.toString(),
      "github_url" to Constants.GITHUB_URL
    )
    inputs.properties(replacements)
    filesMatching("fabric.mod.json") {
      expand(replacements)
    }
  }
}

publishMods.modrinth {
  modLoaders.add("fabric")
  minecraftVersions.addAll(minecraftVersion)
  optional("miniplaceholders")
}
