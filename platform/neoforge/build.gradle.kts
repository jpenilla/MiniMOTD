plugins {
  id("xyz.jpenilla.quiet-architectury-loom")
  id("minimotd.platform-conventions")
  id("io.github.goooler.shadow")
}

val shade: Configuration by configurations.creating

dependencies {
  minecraft(libs.minecraft)
  mappings(loom.officialMojangMappings())
  neoForge(libs.neoforge)

  shade(implementation(projects.minimotdCommon) {
    exclude("net.kyori")
    exclude("com.google.errorprone")
  })

  modImplementation(libs.adventurePlatformNeoforge)
  include(libs.adventurePlatformNeoforge)
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
      "version" to project.version.toString(),
      "description" to project.description.toString(),
      "github_url" to Constants.GITHUB_URL
    )
    inputs.properties(replacements)
    filesMatching("META-INF/neoforge.mods.toml") {
      expand(replacements)
    }
  }
}

publishMods.modrinth {
  modLoaders.add("neoforge")
  minecraftVersions.addAll(minecraftVersion)
}
