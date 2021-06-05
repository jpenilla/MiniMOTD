import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
  id("java-library")
  id("net.kyori.indra")
  id("net.kyori.indra.publishing")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.license-header")
  id("com.adarshr.test-logger")
}

indra {
  javaVersions {
    target(8)
  }
  github(Constants.GITHUB_USER, Constants.GITHUB_REPO)
  mitLicense()
}

testlogger {
  theme = ThemeType.MOCHA_PARALLEL
  showPassed = false
}

dependencies {
  testImplementation(libs.jupiterEngine)
}

tasks {
  tasks {
    withType<JavaCompile> {
      options.compilerArgs.add("-Xlint:-processing")
    }
    sequenceOf(javadocJar, javadoc).forEach {
      it.configure {
        onlyIf { false }
      }
    }
  }
}
