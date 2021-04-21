import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import net.kyori.indra.IndraCheckstylePlugin
import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.IndraPlugin
import net.kyori.indra.sonatypeSnapshots
import java.io.ByteArrayOutputStream

plugins {
  `java-library`
  id("net.kyori.indra")
  id("com.github.johnrengelman.shadow")
  id("com.adarshr.test-logger")
  id("net.kyori.blossom") apply false
}

allprojects {
  group = "xyz.jpenilla"
  version = "2.0.3+${lastCommitHash()}-SNAPSHOT"
  description = "Use MiniMessage text formatting in the server list MOTD."
}

ext["url"] = "https://github.com/jpenilla/MiniMOTD"

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
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
  }

  indra {
    javaVersions {
      testWith(8, 11, 16)
      target.set(8)
    }
    github("jpenilla", "MiniMOTD") {
      issues = true
    }
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

fun lastCommitHash(): String = ByteArrayOutputStream().apply {
  exec {
    commandLine = listOf("git", "rev-parse", "--short", "HEAD")
    standardOutput = this@apply
  }
}.toString(Charsets.UTF_8.name()).trim()
