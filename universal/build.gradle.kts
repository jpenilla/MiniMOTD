plugins {
  id("minimotd.platform-conventions")
}

tasks.jar {
  archiveClassifier.set("empty")
}

val shadowPlatforms = setOf(
  rootProject.projects.minimotdBukkit,
  rootProject.projects.minimotdBungeecord,
  rootProject.projects.minimotdVelocity,
  rootProject.projects.minimotdSponge7,
  //rootProject.projects.minimotdSponge8
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
  artifacts.add("archives", this)
  archiveClassifier.set(null as String?)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  for (platform in shadowPlatforms) {
    val shadowJar = platform.tasks.getByName("shadowJar", AbstractArchiveTask::class)
    from(zipTree(shadowJar.archiveFile))
    dependsOn(shadowJar)
  }

  val fabric = rootProject.projects.minimotdFabric.dependencyProject
  val fabricRemapJarTask = fabric.tasks.getByName("remapJar", AbstractArchiveTask::class)
  from(zipTree(fabricRemapJarTask.archiveFile))
  dependsOn(fabricRemapJarTask)
}

miniMOTDPlatformExtension {
  jarTask.set(universal)
}
