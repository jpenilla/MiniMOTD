import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
  id("minimotd.shadow-platform")
  alias(libs.plugins.sponge.gradle.plugin)
}

dependencies {
  implementation(projects.minimotdCommon)
}

sponge {
  injectRepositories(false)
  apiVersion("8.1.0-SNAPSHOT")
  plugin(rootProject.name.lowercase()) {
    loader {
      name(PluginLoaders.JAVA_PLAIN)
      version("1.0")
    }
    license("MIT")
    displayName(rootProject.name)
    entrypoint("xyz.jpenilla.minimotd.sponge8.MiniMOTDPlugin")
    description(project.description)
    links {
      homepage(Constants.GITHUB_URL)
      source(Constants.GITHUB_URL)
      issues("${Constants.GITHUB_URL}/issues")
    }
    contributor("jmp") {
      description("Lead Developer")
    }
    dependency("spongeapi") {
      loadOrder(PluginDependency.LoadOrder.AFTER)
      optional(false)
    }
  }
}

tasks {
  shadowJar {
    configureForNativeAdventurePlatform()
    dependencies {
      exclude(dependency("io.leangen.geantyref:geantyref"))
    }
  }
}

publishMods.modrinth {
  modLoaders.add("sponge")
  minecraftVersions.addAll(
    "1.16.5",
    "1.17.1",
    "1.18.2",
    "1.19.4",
    "1.20.2"
  )
}
