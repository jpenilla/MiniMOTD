import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import net.kyori.indra.IndraCheckstylePlugin
import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.IndraPlugin
import net.kyori.indra.repository.sonatypeSnapshots

plugins {
  id("minimotd-build-logic")
  id("com.adarshr.test-logger")
}

val projectVersion = "2.0.3-SNAPSHOT"
  .run { if (this.endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this }

allprojects {
  group = "xyz.jpenilla"
  version = projectVersion
  description = "Use MiniMessage text formatting in the server list MOTD."
}

subprojects {
  apply<JavaLibraryPlugin>()
  apply<ShadowPlugin>()
  apply<IndraPlugin>()
  apply<IndraCheckstylePlugin>()
  apply<IndraLicenseHeaderPlugin>()
  apply<TestLoggerPlugin>()

  repositories {
    //mavenLocal()
    mavenCentral()
    sonatypeSnapshots()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public")
  }

  dependencies {
    testImplementation(rootProject.libs.jupiterEngine)
  }

  indra {
    javaVersions {
      target(8)
      testWith(8, 11, 16)
    }
    github("jpenilla", "MiniMOTD")
    mitLicense()
  }

  testlogger {
    theme = ThemeType.MOCHA_PARALLEL
    showPassed = false
  }

  tasks {
    shadowJar {
      minimize()
      exclude("META-INF/versions/**")
      dependencies {
        exclude(dependency("org.checkerframework:checker-qual"))
        exclude(dependency("org.jetbrains:annotations")) // transitive dep from adventure
      }
      commonRelocation("org.spongepowered.configurate")
      commonRelocation("com.typesafe.config")
      if (project != rootProject.projects.minimotdFabric.dependencyProject) {
        archiveClassifier.set("")
        doLast {
          val output = outputs.files.singleFile
          output.copyTo(rootProject.buildDir.resolve("libs").resolve(output.name), overwrite = true)
        }
      }
    }
    withType<JavaCompile> {
      options.compilerArgs.add("-Xlint:-processing")
    }
    withType<Jar> {
      onlyIf { archiveClassifier.get() != "javadoc" }
    }
    withType<Javadoc> {
      onlyIf { false }
    }
  }
}

tasks.withType<Jar> {
  onlyIf { false }
}
