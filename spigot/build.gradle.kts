plugins {
  id("net.minecrell.plugin-yml.bukkit")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.slf4jJdk14)
  implementation(libs.adventurePlatformBukkit)
  implementation(libs.bstatsBukkit)
  compileOnly(libs.paperApi)
}

tasks {
  generateBukkitPluginDescription {
    mustRunAfter(clean)
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
  build {
    dependsOn(shadowJar)
  }
}

bukkit {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.spigot.MiniMOTDPlugin"
  apiVersion = "1.13"
  website = rootProject.ext["url"].toString()
  authors = listOf("jmp")
  softDepend = listOf("ViaVersion")
  commands {
    create("minimotd") {
      description = "MiniMOTD Command"
      usage = "/minimotd help"
    }
  }
}
