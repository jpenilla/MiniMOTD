plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.run.waterfall)
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.adventurePlatformBungeecord)
  implementation(libs.bstatsBungeecord)
  compileOnly(libs.waterfallApi)
}

tasks {
  shadowJar {
    platformRelocation("bungee", "xyz.jpenilla.minimotd.common")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
  runWaterfall {
    waterfallVersion("1.20")
  }
  processResources {
    val props = mapOf(
      "version" to project.version,
      "description" to project.description
    )
    inputs.properties(props)
    filesMatching("bungee.yml") {
      expand(props)
    }
  }
}

modrinth {
  loaders.add("waterfall") // not sure why this isn't detected automatically from run-waterfall...
  gameVersions.addAll(minecraftVersion)
}
