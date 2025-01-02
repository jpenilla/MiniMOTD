plugins {
  id("minimotd.platform-conventions")
}

tasks.jar {
  archiveClassifier.set("empty")
}

val platforms = setOf(
  projects.minimotdBukkit,
  projects.minimotdBungeecord,
)

val platformsConfig = configurations.register("platforms") {
  attributes.attribute(productionJarAttribute, "true")
  isCanBeConsumed = false
  isCanBeResolved = true
}

dependencies {
  for (platform in platforms) {
    platformsConfig.name(platform)
  }
}

val dist = tasks.register<Jar>("bukkitAndBungeeJar") {
  artifacts.add("archives", this)
  archiveClassifier.set(null as String?)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(platformsConfig.flatMap { it.elements }.map { it.map { e -> zipTree(e) } })
}

miniMOTDPlatform {
  jarTask.set(dist)
}
