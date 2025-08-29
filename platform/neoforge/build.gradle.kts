plugins {
  id("net.neoforged.moddev")
  id("minimotd.platform-conventions")
  id("com.gradleup.shadow")
}

neoForge {
  enable {
    version = libs.versions.neoforge.get()
  }
}

val shade: Configuration by configurations.creating

dependencies {
  shade(implementation(projects.minimotdCommon) {
    exclude("net.kyori")
    exclude("com.google.errorprone")
  })

  implementation(libs.adventurePlatformNeoforge)
  jarJar(libs.adventurePlatformNeoforge)
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

val productionJar = tasks.register<Zip>("productionJar") {
  archiveFileName = "${project.name}-mc$minecraftVersion-${project.version}.jar"
  destinationDirectory = layout.buildDirectory.dir("libs")
  from(zipTree(tasks.shadowJar.flatMap { it.archiveFile }))
  from(tasks.jarJar.flatMap { it.outputDirectory })
}

miniMOTDPlatform {
  jarTask.set(productionJar)
}

publishMods.modrinth {
  modLoaders.add("neoforge")
  minecraftVersions.addAll(minecraftVersion)
  optional("miniplaceholders")
}
