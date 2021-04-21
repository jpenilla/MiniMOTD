dependencies {
  implementation(projects.minimotdCommon) {
    exclude("org.slf4j", "slf4j-api")
  }
  implementation(libs.adventurePlatformSpongeApi)
  compileOnly(libs.spongeApi7)
  implementation(libs.bstatsSponge)
}

tasks {
  processResources {
    filesMatching("mcmod.info") {
      mapOf(
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{project.version}" to project.version.toString(),
        "{project.description}" to project.description.toString(),
        "{url}" to rootProject.ext["url"].toString()
      ).forEach { (k, v) ->
        filter { resource -> resource.replace(k, v) }
      }
    }
  }
  shadowJar {
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
  build {
    dependsOn(shadowJar)
  }
}
