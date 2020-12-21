import java.io.ByteArrayOutputStream

plugins {
    `java-library`
}

allprojects {
    group = "xyz.jpenilla"
    version = "1.2.3+${latestCommitHash()}-SNAPSHOT"
    description = "Use MiniMessage text formatting in your servers MOTD."
}

ext["url"] = "https://github.com/jmanpenilla/MiniMOTD/"

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://nexus.velocitypowered.com/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.jpenilla.xyz/snapshots/")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://jitpack.io")
        mavenLocal()
    }

    dependencies {
        annotationProcessor("org.projectlombok", "lombok", "1.18.16")
        compileOnly("org.projectlombok", "lombok", "1.18.16")
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
