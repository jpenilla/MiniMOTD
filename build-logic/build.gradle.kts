plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  val indraVersion = "2.0.4"
  implementation("net.kyori", "indra-common", indraVersion)
  implementation("net.kyori", "indra-git", indraVersion)
  val shadowVersion = "7.0.0"
  implementation("gradle.plugin.com.github.jengelman.gradle.plugins", "shadow", shadowVersion)
}

gradlePlugin {
  plugins {
    create("minimotd-build-logic") {
      id = "minimotd-build-logic"
      implementationClass = "BuildLogicPlugin"
    }
  }
}
