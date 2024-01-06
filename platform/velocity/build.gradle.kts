plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.run.velocity)
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.bstatsVelocity)
  compileOnly(libs.velocityApi)
}

java.disableAutoTargetJvm()

tasks {
  shadowJar {
    configureForNativeAdventurePlatform()
    commonRelocation("io.leangen.geantyref")
    commonRelocation("org.bstats")
  }
  runVelocity {
    velocityVersion(libs.versions.velocityApi.get())
  }
  processResources {
    val props = mapOf(
      "description" to project.description as String,
      "url" to Constants.GITHUB_URL,
      "version" to project.version,
    )
    inputs.properties(props)
    filesMatching("velocity-plugin.json") {
      expand(props)
    }
  }
}

publishMods.modrinth {
  modLoaders.add("velocity")
  minecraftVersions.addAll(minecraftVersion)
}
