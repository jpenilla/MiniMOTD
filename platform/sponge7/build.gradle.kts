import java.util.Locale

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
      "modid" to rootProject.name.lowercase(Locale.ENGLISH),
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

modrinth {
  loaders.add("sponge")
  gameVersions.add("1.12.2")
}
