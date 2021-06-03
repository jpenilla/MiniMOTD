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
  //projects.minimotdSponge8
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
  artifacts.add("archives", this)
  archiveClassifier.set(null as String?)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  for (platform in platforms) {
    val jarTask = platform.miniMOTDPlatform.jarTask
    from(zipTree(jarTask.flatMap { it.archiveFile }))
    dependsOn(jarTask) // todo: remove when updating Gradle to 7.1 (https://github.com/gradle/gradle/issues/15569)
  }
}

miniMOTDPlatform {
  jarTask.set(universal)
}
