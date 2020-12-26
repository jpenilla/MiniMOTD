import net.kyori.blossom.BlossomExtension

plugins {
    id("net.kyori.blossom") version "1.1.0"
}

dependencies {
    implementation(project(":minimotd-common"))
    compileOnly("com.velocitypowered", "velocity-api", "1.1.2")
    annotationProcessor("com.velocitypowered", "velocity-api", "1.1.2")
}

tasks {
    shadowJar {
        relocate("net.kyori.adventure.text.minimessage", "xyz.jpenilla.minimotd.velocity.lib.minimessage")
        relocate("xyz.jpenilla.minimotd.common", "xyz.jpenilla.minimotd.velocity.lib.minimotd.common")
        relocate("org.checkerframework", "xyz.jpenilla.minimotd.lib.checkerframework")
        dependencies {
            exclude { dep -> dep.moduleGroup == "net.kyori" && !dep.name.contains("minimessage") }
        }
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
