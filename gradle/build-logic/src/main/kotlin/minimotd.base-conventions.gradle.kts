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
    target(17)
    minimumToolchain(21)
    testWith(17, 21, 25)
  }
  github(Constants.GITHUB_USER, Constants.GITHUB_REPO)
  mitLicense()
}

java.disableAutoTargetJvm()

repositories {
  mavenCentral {
    mavenContent { releasesOnly() }
  }
  maven("https://repo.jpenilla.xyz/snapshots/") {
    mavenContent {
      snapshotsOnly()
      includeGroup("xyz.jpenilla")
    }
  }
  maven("https://central.sonatype.com/repository/maven-snapshots/") {
    mavenContent { snapshotsOnly() }
  }
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
  testImplementation(libs.junitJupiter)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
  withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-processing")
  }
  listOf(javadocJar, javadoc).forEach {
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
