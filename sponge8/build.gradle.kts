import org.spongepowered.gradle.plugin.config.PluginLoaders.JAVA_PLAIN
import org.spongepowered.plugin.metadata.PluginDependency.LoadOrder.AFTER

plugins {
  id("org.spongepowered.gradle.plugin") version "1.0.1"
}

dependencies {
  implementation(project(":minimotd-common"))
}

sponge {
  apiVersion("8.0.0")
  plugin(project.name) {
    loader(JAVA_PLAIN)
    displayName(rootProject.name)
    mainClass("xyz.jpenilla.minimotd.sponge8.MiniMOTDPlugin")
    description(project.description)
    links {
      val url = rootProject.ext["url"].toString()
      homepage(url)
      source(url)
      issues("${url}issues/")
    }
    contributor("jmp") {
      description("Lead Developer")
    }
    dependency("spongeapi") {
      loadOrder(AFTER)
      optional(false)
    }
  }
}

tasks {
  shadowJar {
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.lib.kyori_native.minimessage")
    relocate("net.kyori.adventure.text.serializer.legacy", "xyz.jpenilla.minimotd.lib.kyori_native.legacy.text.serializer")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.lib.sponge8.minimotd.common")
    dependencies {
      exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") && !dep.name.contains("text-serializer-legacy") }
      exclude(dependency("org.slf4j:slf4j-api"))
    }
  }
  runServer {
    classpath(shadowJar)
  }
  build {
    dependsOn(shadowJar)
  }
}
