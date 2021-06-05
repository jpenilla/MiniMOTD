plugins {
  id("minimotd.base-conventions")
  id("net.kyori.indra.git")
}

val platformExtension = extensions.create<MiniMOTDPlatformExtension>("miniMOTDPlatform", project)

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
