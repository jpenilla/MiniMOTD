plugins {
  id("minimotd.shadow-platform")
  id("net.kyori.blossom")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.bstatsVelocity)
  compileOnly(libs.velocityApi)
  annotationProcessor(libs.velocityApi)
}

tasks {
  shadowJar {
    configureForNativeAdventurePlatform()
    commonRelocation("io.leangen.geantyref")
    platformRelocation("velocity", "xyz.jpenilla.minimotd.common")
    commonRelocation("org.bstats")
  }
}

blossom {
  val file = "src/main/java/xyz/jpenilla/minimotd/velocity/MiniMOTDPlugin.java"
  mapOf(
    "project.name" to project.name,
    "description" to description as String,
    "url" to Constants.GITHUB_URL
  ).forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
}
