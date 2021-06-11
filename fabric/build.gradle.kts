plugins {
  id("minimotd.platform-conventions")
  id("com.github.johnrengelman.shadow")
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
}

miniMOTDPlatform {
  jarTask.set(tasks.remapJar)
}

indra {
  javaVersions {
    target(16)
  }
}

tasks {
  runServer {
    standardInput = System.`in`
  }
  shadowJar {
    configurations = listOf(shade)
    commonConfiguration()
    commonRelocation("io.leangen.geantyref")
    platformRelocation("fabric", "xyz.jpenilla.minimotd.common")
  }
  remapJar {
    input.set(shadowJar.flatMap { it.archiveFile })
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
