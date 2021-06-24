plugins {
  id("minimotd.shadow-platform")
  id("net.minecrell.plugin-yml.bukkit")
  id("xyz.jpenilla.run-paper")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.slf4jJdk14)
  implementation(libs.adventurePlatformBukkit)
  implementation(libs.bstatsBukkit)
  compileOnly(libs.paperApi)
}

tasks {
  runServer {
    minecraftVersion("1.17")
  }
  generateBukkitPluginDescription {
    mustRunAfter(clean)
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
}

bukkit {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.bukkit.MiniMOTDPlugin"
  apiVersion = "1.13"
  website = Constants.GITHUB_URL
  authors = listOf("jmp")
  softDepend = listOf("ViaVersion")
  commands {
    create("minimotd") {
      description = "MiniMOTD Command"
      usage = "/minimotd help"
      permission = "minimotd.admin"
    }
  }
}
