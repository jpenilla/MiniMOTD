plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.run.paper)
  alias(libs.plugins.resource.factory.bukkit)
  alias(libs.plugins.resource.factory.paper)
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.bstatsBukkit)
  compileOnly(libs.paperApi)
}

tasks {
  runServer {
    minecraftVersion(minecraftVersion)
  }
  shadowJar {
    configureForNativeAdventurePlatform()
    commonRelocation("org.bstats")
    dependencies {
      exclude(dependency("io.leangen.geantyref:geantyref"))
    }
  }
}

indra {
  javaVersions {
    target(21)
  }
}

bukkitPluginYaml {
  name = "MiniMOTD"
  main = "xyz.jpenilla.minimotd.paper.MiniMOTDStub"
  foliaSupported = true
  authors = listOf("jmp")
  website = Constants.GITHUB_URL
  apiVersion = "1.13"
}

paperPluginYaml {
  name = "MiniMOTD"
  main = "xyz.jpenilla.minimotd.paper.MiniMOTDPaper"
  foliaSupported = true
  authors = listOf("jmp")
  website = Constants.GITHUB_URL
  apiVersion = "1.21.8"
}

runPaper.folia.registerTask()

publishMods.modrinth {
  modLoaders.addAll("paper", "folia")
  minecraftVersions = paperVersions
}
