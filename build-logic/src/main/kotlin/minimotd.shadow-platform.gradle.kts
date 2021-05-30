plugins {
  id("minimotd.platform-conventions")
  id("com.github.johnrengelman.shadow")
}

tasks {
  jar {
    archiveClassifier.set("unshaded")
  }
  shadowJar {
    archiveClassifier.set(null as String?)
    commonConfiguration()
  }
}

extensions.configure<MiniMOTDPlatformExtension> {
  jarTask.set(tasks.named<AbstractArchiveTask>("shadowJar"))
}
