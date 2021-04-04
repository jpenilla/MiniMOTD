plugins {
  id("net.minecrell.plugin-yml.bungee") version "0.3.0"
}

dependencies {
  implementation(project(":minimotd-common"))
  implementation("org.slf4j", "slf4j-jdk14","1.7.30")
  implementation("net.kyori", "adventure-platform-bungeecord", "4.0.0-SNAPSHOT")
  implementation("org.bstats", "bstats-bungeecord", "2.2.1")
  compileOnly("io.github.waterfallmc", "waterfall-api", "1.16-R0.4-SNAPSHOT")
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

bungee {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.bungee.MiniMOTDPlugin"
  author = "jmp"
}
