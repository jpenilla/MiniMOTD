plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":minimotd-spigot"))
    implementation(project(":minimotd-bungeecord"))
    implementation(project(":minimotd-velocity"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            exclude { dep ->
                !dep.name.contains("minimotd") && !dep.name.contains("minimessage")
            }
        }
        relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.velocity.lib.minimessage")
    }
    build {
        dependsOn(shadowJar)
    }
}
