import org.spongepowered.gradle.plugin.config.PluginLoaders.JAVA_PLAIN
import org.spongepowered.plugin.metadata.PluginDependency.LoadOrder.AFTER

plugins {
  id("org.spongepowered.gradle.plugin") version "1.0.3"
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.slf4jLog4jImpl) {
    isTransitive = false
  }
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
      issues("${url}/issues")
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
    relocate("org.apache.logging.slf4j", "xyz.jpenilla.minimotd.lib.apache.logging.slf4j")
    relocate("org.slf4j", "xyz.jpenilla.minimotd.lib.slf4j_log4j")
    relocate("org.spongepowered.configurate", "xyz.jpenilla.minimotd.lib.spongepowered.configurate")
    relocate("io.leangen.geantyref", "xyz.jpenilla.minimotd.lib.io.leangen.geantyref")
    relocate("com.typesafe.config", "xyz.jpenilla.minimotd.lib.typesafe.config")
    relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.lib.kyori_native.minimessage")
    relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.lib.sponge8.minimotd.common")
    dependencies {
      exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") }
    }
  }
  runServer {
    classpath(shadowJar)
  }
  build {
    dependsOn(shadowJar)
  }
}
