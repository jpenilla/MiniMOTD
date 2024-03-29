plugins {
  id("minimotd.shadow-platform")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.adventurePlatformSpongeApi)
  compileOnly(libs.spongeApi7)
  implementation(libs.bstatsSponge)
}

tasks {
  processResources {
    val replacements = mapOf(
      "modid" to rootProject.name.lowercase(),
      "name" to rootProject.name,
      "version" to project.version.toString(),
      "description" to project.description.toString(),
      "url" to Constants.GITHUB_URL
    )
    inputs.properties(replacements)
    filesMatching("mcmod.info") {
      expand(replacements)
    }
  }
  shadowJar {
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
}

publishMods.modrinth {
  modLoaders.add("sponge")
  minecraftVersions.add("1.12.2")
}
