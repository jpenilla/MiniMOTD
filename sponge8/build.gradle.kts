dependencies {
  implementation(project(":minimotd-common"))
  implementation("net.kyori", "adventure-text-serializer-legacy", "4.4.0")
  compileOnly("org.spongepowered", "spongeapi", "8.0.0-SNAPSHOT")
  annotationProcessor("org.spongepowered", "spongeapi", "8.0.0-SNAPSHOT")
}

tasks {
  shadowJar {
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.lib.kyori_native.minimessage")
    relocate("net.kyori.adventure.text.serializer.legacy", "xyz.jpenilla.minimotd.lib.kyori_native.legacy.text.serializer")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.sponge8.lib.minimotd.common")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    dependencies {
      exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") && !dep.name.contains("text-serializer-legacy") }
      exclude(dependency("org.slf4j:slf4j-api"))
    }
  }
  processResources {
    filesMatching("**/plugins.json") {
      mapOf(
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{version}" to version.toString(),
        "{description}" to project.description,
        "{url}" to rootProject.ext["url"].toString()
      ).entries.forEach { (k, v) -> filter { it.replace(k, v as String) } }
    }
  }
  build {
    dependsOn(shadowJar)
  }
}
