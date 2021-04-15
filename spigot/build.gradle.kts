plugins {
  id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
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
    relocate("org.slf4j", "xyz.jpenilla.minimotd.lib.slf4j")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori", "xyz.jpenilla.minimotd.lib.kyori")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("org.bstats", "xyz.jpenilla.minimotd.lib.bstats")
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
