plugins {
  id("minimotd.shadow-platform")
  id("xyz.jpenilla.run-paper")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.bstatsBukkit)
  compileOnly(libs.paperApi)
}

tasks {
  runServer {
    minecraftVersion(minecraftVersion)
  }
  shadowJar {
    configureForNativeAdventurePlatform()
    commonRelocation("org.bstats")
    dependencies {
      // Already included in Paper 1.18.2+
      exclude(dependency("io.leangen.geantyref:geantyref"))
    }
  }
  processResources {
    filesMatching("paper-plugin.yml") {
      expand("version" to version)
    }
  }
}

modrinth {
  gameVersions.addAll(paperVersions)
}
