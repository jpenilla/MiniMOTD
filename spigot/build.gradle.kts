plugins {
  id("kr.entree.spigradle") version "2.2.3"
}

dependencies {
  implementation(project(":minimotd-common"))
  implementation("org.slf4j", "slf4j-jdk14","1.7.30")
  implementation("net.kyori", "adventure-platform-bukkit", "4.0.0-SNAPSHOT")
  implementation("org.bstats", "bstats-bukkit", "2.2.1")
  compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
}

tasks {
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

spigot {
  name = rootProject.name
  apiVersion = "1.13"
  website = rootProject.ext["url"].toString()
  authors("jmp")
  softDepends("Prisma", "ViaVersion")
  commands {
    create("minimotd") {
      description = "MiniMOTD Command"
      permissionMessage = "No permission"
      usage = "/minimotd help"
    }
  }
}
