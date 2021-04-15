import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks {
  val universal = register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set("")
    destinationDirectory.set(rootProject.buildDir.resolve("libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    sequenceOf(
      rootProject.projects.minimotdSpigot,
      rootProject.projects.minimotdBungeecord,
      rootProject.projects.minimotdVelocity,
      rootProject.projects.minimotdSponge7,
      //rootProject.projects.minimotdSponge8
    ).map { it.dependencyProject }.forEach { subproject ->
      val shadowJar = subproject.tasks.getByName("shadowJar", ShadowJar::class)
      from(zipTree(shadowJar.archiveFile))
      dependsOn(subproject.tasks.withType<Jar>())
      dependsOn(shadowJar)
    }
    val fabric = rootProject.projects.minimotdFabric.dependencyProject
    val fabricRemapJarTask = fabric.tasks.getByName("remapJar", org.gradle.jvm.tasks.Jar::class)
    dependsOn(fabric.tasks.withType<Jar>())
    dependsOn(fabricRemapJarTask)
    from(zipTree(fabricRemapJarTask.archiveFile))
  }
  build {
    dependsOn(universal)
  }
}
