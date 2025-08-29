plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.run.velocity)
  alias(libs.plugins.resource.factory.velocity)
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
}

velocityPluginJson {
  name = "MiniMOTD"
  id = "minimotd-velocity"
  url = Constants.GITHUB_URL
  authors = listOf("jmp")
  main = "xyz.jpenilla.minimotd.velocity.MiniMOTDVelocity"
  dependency("miniplaceholders", true)
}

publishMods.modrinth {
  modLoaders.add("velocity")
  minecraftVersions.addAll(minecraftVersion)
  optional("miniplaceholders")
}
