plugins {
  id("minimotd.platform-conventions")
}

tasks.jar {
  archiveClassifier.set("empty")
}

val platforms = setOf(
  projects.minimotdBukkit,
  projects.minimotdBungeecord,
  projects.minimotdVelocity,
  projects.minimotdFabric,
  projects.minimotdSponge7,
  projects.minimotdSponge8
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
  artifacts.add("archives", this)
  archiveClassifier.set(null as String?)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  for (platform in platforms) {
    val jarTask = platform.miniMOTDPlatform.jarTask
    from(zipTree(jarTask.flatMap { it.archiveFile }))
  }
}

miniMOTDPlatform {
  jarTask.set(universal)
}
