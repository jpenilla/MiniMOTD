import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import net.kyori.indra.IndraCheckstylePlugin
import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.IndraPlugin
import net.kyori.indra.sonatypeSnapshots
import java.io.ByteArrayOutputStream

plugins {
  `java-library`
  id("net.kyori.indra") version "1.3.1"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

allprojects {
  group = "xyz.jpenilla"
  version = "1.3.0+${latestCommitHash()}-SNAPSHOT"
  description = "Use MiniMessage text formatting in your servers MOTD."
}

ext["url"] = "https://github.com/jmanpenilla/MiniMOTD/"

subprojects {
  apply<JavaLibraryPlugin>()
  apply<ShadowPlugin>()
  apply<IndraPlugin>()
  apply<IndraCheckstylePlugin>()
  apply<IndraLicenseHeaderPlugin>()

  repositories {
    //mavenLocal()
    mavenCentral()
    sonatypeSnapshots()
    jcenter()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    //maven("https://repo-new.spongepowered.org/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public")
  }

  indra {
    javaVersions.target.set(8)
    github("jpenilla", "MiniMOTD") {
      issues = true
    }
    mitLicense()
  }

  dependencies {
    annotationProcessor("org.projectlombok", "lombok", "1.18.16")
    compileOnly("org.projectlombok", "lombok", "1.18.16")
  }

  tasks {
    shadowJar {
      minimize()
      if (!project.name.contains("fabric")) {
        archiveClassifier.set("")
      }
    }
    withType<JavaCompile> {
      options.compilerArgs.add("-Xlint:-processing")
    }
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

allprojects {
  tasks.withType<Jar> {
    onlyIf {
      val classifier = archiveClassifier.get()
      classifier != "javadoc"
        && project.name != rootProject.name
    }
  }
  tasks.withType<Javadoc> {
    onlyIf { false }
  }
}

configurations.archives {
  artifacts.removeAll { true }
}

tasks {
  val aggregate = create("aggregateJars") {
    val artifacts = arrayListOf<File>()
    dependsOn(project(":minimotd-universal").tasks.getByName("build"))
    arrayOf("spigot", "bungeecord", "velocity"/*, "sponge8"*/).forEach {
      val subProject = project(":minimotd-$it")
      val shadow = subProject.tasks.getByName("shadowJar")
      artifacts.add(shadow.outputs.files.singleFile)
    }
    doLast {
      artifacts.add(project(":minimotd-fabric").tasks.getByName("remapJar").outputs.files.singleFile)
      artifacts.add(project(":minimotd-universal").project.tasks.getByName("universal").outputs.files.singleFile)
      val libs = rootProject.buildDir.resolve("libs")
      libs.listFiles()?.forEach { it.delete() }
      artifacts.forEach { it.copyTo(libs.resolve(it.name)) }
    }
  }
  build {
    dependsOn(aggregate)
  }
}

fun latestCommitHash(): String {
  val byteOut = ByteArrayOutputStream()
  exec {
    commandLine = listOf("git", "rev-parse", "--short", "HEAD")
    standardOutput = byteOut
  }
  return byteOut.toString(Charsets.UTF_8.name()).trim()
}
