import com.modrinth.minotaur.ModrinthExtension

plugins {
  id("minimotd.base-conventions")
  id("net.kyori.indra.git")
}

val platformExtension = extensions.create<MiniMOTDPlatformExtension>("miniMOTDPlatform")

tasks {
  val copyJar = register<FileCopyTask>("copyJar") {
    fileToCopy.set(platformExtension.jarTask.flatMap { it.archiveFile })
    destination.set(rootProject.layout.buildDirectory.dir("libs").flatMap {
      it.file(fileToCopy.map { file -> file.asFile.name })
    })
  }
  build {
    dependsOn(copyJar)
  }
}

if (name != "minimotd-bukkit-bungeecord") {
  plugins.apply("com.modrinth.minotaur")

  the<ModrinthExtension>().apply {
    projectId.set("16vhQOQN")
    versionType.set("release")
    file.set(platformExtension.jarTask.flatMap { it.archiveFile })
    changelog.set(releaseNotes)
    token.set(providers.environmentVariable("MODRINTH_TOKEN"))
  }
}
