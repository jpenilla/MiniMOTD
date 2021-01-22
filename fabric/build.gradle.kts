plugins {
  id("fabric-loom") version "0.5-SNAPSHOT"
  id("net.kyori.blossom") version "1.1.0"
}

configurations {
  create("shade")
}

dependencies {
  minecraft("com.mojang", "minecraft", "1.16.5")
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc", "fabric-loader", "0.11.1")
  modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.29.4+1.16")

  modImplementation(include("net.kyori", "adventure-platform-fabric", "4.0.0-SNAPSHOT"))
  implementation(include("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT"))

  add("shade", implementation("org.slf4j", "slf4j-jdk14","1.7.30"))
  add("shade", implementation(project(":minimotd-common")) {
    exclude("net.kyori", "adventure-text-minimessage")
    exclude("net.kyori", "adventure-api")
  })
}

tasks {
  shadowJar {
    configurations = listOf(project.configurations.getByName("shade"))
    relocate("org.slf4j", "xyz.jpenilla.minimotd.lib.slf4j")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
  }
  remapJar {
    dependsOn(shadowJar)
    input.set(shadowJar.get().outputs.files.singleFile)
  }
  processResources {
    filesMatching("fabric.mod.json") {
      mapOf(
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{version}" to version.toString(),
        "{description}" to project.description,
        "{url}" to rootProject.ext["url"].toString()
      ).entries.forEach { (k, v) -> filter { it.replace(k, v as String) } }
    }
  }
}

blossom {
  replaceToken("{version}", version.toString(), "src/main/java/xyz/jpenilla/minimotd/fabric/MiniMOTDFabric.java")
}
