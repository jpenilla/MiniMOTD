plugins {
  id("minimotd.platform-conventions")
  id("com.gradleup.shadow")
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
