import me.modmuss50.mpp.ModPublishExtension

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
  plugins.apply("me.modmuss50.mod-publish-plugin")

  extensions.configure(ModPublishExtension::class) {
    modrinth {
      projectId.set("16vhQOQN")
      type = STABLE
      file.set(platformExtension.jarTask.flatMap { it.archiveFile })
      changelog.set(releaseNotes)
      accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
    }
  }
}

configurations.consumable("productionJar") {
  outgoing {
    artifact(platformExtension.jarTask)
  }
  attributes.attribute(productionJarAttribute, "true")
}
