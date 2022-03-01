plugins {
  id("minimotd.shadow-platform")
  id("net.minecrell.plugin-yml.bungee")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.adventurePlatformBungeecord)
  implementation(libs.bstatsBungeecord)
  compileOnly(libs.waterfallApi)
}

tasks {
  shadowJar {
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
}

bungee {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.bungee.MiniMOTDPlugin"
  author = "jmp"
}
