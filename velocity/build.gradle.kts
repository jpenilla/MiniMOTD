plugins {
  id("net.kyori.blossom")
}

dependencies {
  implementation(projects.minimotdCommon) {
    exclude("org.slf4j", "slf4j-api")
  }
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
  build {
    dependsOn(shadowJar)
  }
}

blossom {
  fun replaceTokens(file: String, vararg tokens: Pair<String, String>) = tokens.forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
  replaceTokens(
    "src/main/java/xyz/jpenilla/minimotd/velocity/MiniMOTDPlugin.java",
    "project.name" to project.name,
    "description" to description!!,
    "url" to rootProject.ext["url"].toString()
  )
}
