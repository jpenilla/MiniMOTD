plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  val indraVersion = "2.0.5"
  implementation("net.kyori", "indra-common", indraVersion)
  implementation("net.kyori", "indra-git", indraVersion)
  implementation("gradle.plugin.com.github.jengelman.gradle.plugins", "shadow", "7.0.0")
  implementation("com.adarshr", "gradle-test-logger-plugin", "3.0.0")
}
