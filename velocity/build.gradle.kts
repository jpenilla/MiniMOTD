import net.kyori.blossom.BlossomExtension

plugins {
  id("net.kyori.blossom") version "1.1.0"
}

dependencies {
  implementation(project(":minimotd-common")) {
    exclude("org.slf4j", "slf4j-api")
  }
  compileOnly("com.velocitypowered", "velocity-api", "1.1.2")
  annotationProcessor("com.velocitypowered", "velocity-api", "1.1.2")
}

tasks {
  shadowJar {
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.lib.kyori_native.minimessage")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.lib.kyori_native.minimotd.common")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    dependencies {
      exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") }
    }
  }
  build {
    dependsOn(shadowJar)
  }
}

blossom {
  replaceTokens(
    "src/main/java/xyz/jpenilla/minimotd/velocity/MiniMOTDPlugin.java",
    "{project.name}" to project.name,
    "{rootProject.name}" to rootProject.name,
    "{version}" to version.toString(),
    "{description}" to description!!,
    "{url}" to rootProject.ext["url"].toString()
  )
}

fun BlossomExtension.replaceTokens(
  filePath: String,
  vararg replacements: Pair<String, String>
) {
  for ((token, replacement) in replacements) replaceToken(token, replacement, filePath)
}
