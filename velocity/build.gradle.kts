import net.kyori.blossom.BlossomExtension

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.kyori.blossom") version "1.1.0"
}

dependencies {
    implementation(project(":minimotd-common"))
    compileOnly("com.velocitypowered", "velocity-api", "1.1.2")
    annotationProcessor("com.velocitypowered", "velocity-api", "1.1.2")
}

tasks {
    shadowJar {
        minimize()
        archiveClassifier.set("")
        relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.velocity.lib.minimessage")
        dependencies {
            exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") }
        }
        relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
    }
    build {
        dependsOn(shadowJar)
    }
}

blossom {
    replaceTokens(
        "src/main/java/xyz/jpenilla/minimotd/velocity/MiniMOTD.java",
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{version}" to version.toString(),
        "{description}" to description!!,
        "{url}" to rootProject.ext["url"].toString()
    )
}

fun BlossomExtension.replaceTokens(
    filePath: String,
    vararg replacements: Pair<String, String>
) {
    for ((token, replacement) in replacements) replaceToken(token, replacement, filePath)
}
