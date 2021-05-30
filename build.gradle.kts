import org.gradle.api.plugins.JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME

plugins {
  id("minimotd.build-logic")
}

group = "xyz.jpenilla"
version = "2.0.3-SNAPSHOT"
description = "Use MiniMessage text formatting in the server list MOTD."

subprojects {
  plugins.apply("minimotd.base-conventions")

  dependencies {
    TEST_IMPLEMENTATION_CONFIGURATION_NAME(rootProject.libs.jupiterEngine)
  }
}
