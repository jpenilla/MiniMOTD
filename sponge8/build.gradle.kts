import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency
import java.util.Locale

plugins {
  id("minimotd.shadow-platform")
  id("org.spongepowered.gradle.plugin")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.log4jSlf4jImpl) {
    isTransitive = false
  }
}

sponge {
  injectRepositories(false)
  apiVersion("8.0.0")
  plugin(rootProject.name.toLowerCase(Locale.ENGLISH)) {
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
    commonRelocation("org.apache.logging.slf4j")
    platformRelocation("log4j", "org.slf4j")
    platformRelocation("sponge8", "xyz.jpenilla.minimotd.common")
    dependencies {
      exclude(dependency("io.leangen.geantyref:geantyref"))
    }
  }
}
