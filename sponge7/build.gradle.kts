plugins {
  id("org.spongepowered.plugin") version "0.9.0"
}

dependencies {
  implementation(project(":minimotd-common")) {
    exclude("org.slf4j", "slf4j-api")
  }
  implementation("net.kyori", "adventure-platform-spongeapi", "4.0.0-SNAPSHOT")
  annotationProcessor(compileOnly("org.spongepowered", "spongeapi", "7.2.0"))
  implementation("org.bstats", "bstats-sponge", "2.2.1")
}

tasks {
  shadowJar {
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori", "xyz.jpenilla.minimotd.lib.kyori")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("org.bstats", "xyz.jpenilla.minimotd.lib.bstats")
    exclude("META-INF/versions/**")
  }
  build {
    dependsOn(shadowJar)
  }
}

sponge {
  plugin {
    meta {
      id = project.name
      name = rootProject.name
      authors.add("jmp")
    }
  }
}
