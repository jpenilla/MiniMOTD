plugins {
  id("java-library")
  id("net.kyori.indra")
  id("net.kyori.indra.git")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.licenser.spotless")
}

version = (version as String)
  .run { if (this.endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this }

indra {
  javaVersions {
    minimumToolchain(17)
    target(8)
  }
  github(Constants.GITHUB_USER, Constants.GITHUB_REPO)
  mitLicense()
}

dependencies {
  testImplementation(libs.jupiterEngine)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
  withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-processing")
  }
  sequenceOf(javadocJar, javadoc).forEach {
    it.configure {
      onlyIf { false }
    }
  }
  test {
    testLogging {
      events("passed")
    }
  }
}
