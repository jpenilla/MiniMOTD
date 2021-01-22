tasks {
  val universal = register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    arrayOf("spigot", "bungeecord", "velocity"/*, "sponge8"*/).forEach {
      val shadowJar = rootProject.project(":minimotd-$it").tasks.getByName("shadowJar")
      from(zipTree(shadowJar.outputs.files.singleFile))
      dependsOn(shadowJar)
    }
    val fabricRemapJarTask = rootProject.project(":minimotd-fabric").tasks.getByName("remapJar")
    dependsOn(fabricRemapJarTask)
    from(zipTree(fabricRemapJarTask.outputs.files.singleFile))
  }
  build {
    dependsOn(universal)
  }
}
