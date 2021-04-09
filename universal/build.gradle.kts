import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks {
  val universal = register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set("")
    destinationDirectory.set(rootProject.buildDir.resolve("libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    arrayOf("spigot", "bungeecord", "velocity", "sponge7", "sponge8").forEach {
      val subproject = rootProject.project(":minimotd-$it")
      val shadowJar = subproject.tasks.getByName("shadowJar", ShadowJar::class)
      from(zipTree(shadowJar.archiveFile))
      dependsOn(subproject.tasks.withType<Jar>())
      dependsOn(shadowJar)
    }
    val fabricRemapJarTask = rootProject.project(":minimotd-fabric").tasks.getByName("remapJar", org.gradle.jvm.tasks.Jar::class)
    dependsOn(fabricRemapJarTask)
    from(zipTree(fabricRemapJarTask.archiveFile))
  }
  build {
    dependsOn(universal)
  }
}
