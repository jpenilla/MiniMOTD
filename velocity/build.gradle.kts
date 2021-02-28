plugins {
  id("net.kyori.blossom")
}

dependencies {
  implementation(project(":minimotd-common")) {
    exclude("org.slf4j", "slf4j-api")
  }
  implementation("org.bstats", "bstats-velocity", "2.2.0")
  compileOnly("com.velocitypowered", "velocity-api", "1.1.4")
  annotationProcessor("com.velocitypowered", "velocity-api", "1.1.4")
}

tasks {
  shadowJar {
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.lib.kyori_native.minimessage")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.lib.velocity.minimotd.common")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("org.bstats", "xyz.jpenilla.minimotd.lib.bstats")
    dependencies {
      exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") }
    }
  }
  build {
    dependsOn(shadowJar)
  }
}

blossom {
  fun replaceTokens(file: String, vararg tokens: Pair<String, String>) = tokens.forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
  replaceTokens(
    "src/main/java/xyz/jpenilla/minimotd/velocity/MiniMOTDPlugin.java",
    "project.name" to project.name,
    "description" to description!!,
    "url" to rootProject.ext["url"].toString()
  )
}
