plugins {
  id("minimotd.platform-conventions")
}

tasks.jar {
  archiveClassifier.set("empty")
}

val platforms = setOf(
  projects.minimotdBukkit,
  projects.minimotdBungeecord,
).map { it.dependencyProject }

val dist = tasks.register<Jar>("bukkitAndBungeeJar") {
  artifacts.add("archives", this)
  archiveClassifier.set(null as String?)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  for (platform in platforms) {
    val jarTask = platform.miniMOTDPlatform.jarTask
    from(zipTree(jarTask.flatMap { it.archiveFile }))
  }
}

miniMOTDPlatform {
  jarTask.set(dist)
}
