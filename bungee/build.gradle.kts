plugins {
  id("minimotd.shadow-platform")
  id("net.minecrell.plugin-yml.bungee")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.slf4jJdk14)
  implementation(libs.adventurePlatformBungeecord)
  implementation(libs.bstatsBungeecord)
  compileOnly(libs.waterfallApi)
}

tasks {
  generateBungeePluginDescription {
    mustRunAfter(clean)
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
  build {
    dependsOn(shadowJar)
  }
}

bungee {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.bungee.MiniMOTDPlugin"
  author = "jmp"
}
