plugins {
  id("minimotd.platform-conventions")
  id("io.github.goooler.shadow")
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
  jarTask.set(tasks.shadowJar)
}
