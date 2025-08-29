plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.run.paper)
  alias(libs.plugins.resource.factory.bukkit)
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.slf4jJdk14)
  implementation(libs.adventurePlatformBukkit)
  implementation(libs.bstatsBukkit)
  implementation(libs.paperlib)
  compileOnly(libs.paperApiLegacy)
}

tasks {
  runServer {
    minecraftVersion("1.21.7")
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
    commonRelocation("io.papermc.lib")
    manifest {
      attributes("paperweight-mappings-namespace" to "mojang")
    }
  }
}

bukkitPluginYaml {
  name = "MiniMOTD"
  main = "xyz.jpenilla.minimotd.bukkit.MiniMOTDBukkit"
  foliaSupported = true
  authors = listOf("jmp")
  website = Constants.GITHUB_URL
  commands.register("minimotd") {
    description = "MiniMOTD Command"
    permission = "minimotd.admin"
    usage = "/minimotd help"
  }
  apiVersion = "1.13"
  softDepend = listOf("ViaVersion")
}

runPaper.folia.registerTask()

publishMods.modrinth {
  modLoaders.addAll("paper", "folia")
  minecraftVersions = bukkitVersions
}
