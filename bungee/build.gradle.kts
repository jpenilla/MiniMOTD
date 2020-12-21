plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("kr.entree.spigradle.bungee") version "2.2.3"
}

dependencies {
    implementation(project(":minimotd-common"))
    implementation("net.kyori", "adventure-platform-bungeecord", "4.0.0-SNAPSHOT")
    implementation("org.bstats", "bstats-bungeecord", "1.8")
    compileOnly("io.github.waterfallmc", "waterfall-api", "1.16-R0.4-SNAPSHOT")
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

bungee {
    name = rootProject.name
    author = "jmp"
}
