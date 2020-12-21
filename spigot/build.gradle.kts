plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("kr.entree.spigradle") version "2.2.3"
}

dependencies {
    implementation(project(":minimotd-common"))
    implementation("net.kyori", "adventure-platform-bukkit", "4.0.0-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "1.8")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.4-R0.1-SNAPSHOT")
    compileOnly("com.github.DiamondDagger590", "Prisma", "a622d01b80")
}

tasks {
    shadowJar {
        minimize()
        archiveClassifier.set("")
        relocate("net.kyori", "xyz.jpenilla.minimotd.lib.kyori")
        relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
        relocate("org.bstats", "xyz.jpenilla.minimotd.lib.bstats")
    }
    build {
        dependsOn(shadowJar)
    }
}

spigot {
    name = rootProject.name
    apiVersion = "1.13"
    website = rootProject.ext["url"].toString()
    authors("jmp")
    softDepends("Prisma", "ViaVersion")
    commands {
        create("minimotd") {
            description = "MiniMOTD Command"
            permissionMessage = "No permission"
            usage = "/minimotd help"
        }
    }
}
