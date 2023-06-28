plugins {
  id("minimotd.shadow-platform")
  id("net.minecrell.plugin-yml.bungee")
  id("xyz.jpenilla.run-waterfall")
}

dependencies {
  implementation(projects.minimotdCommon)
  implementation(libs.adventurePlatformBungeecord)
  implementation(libs.bstatsBungeecord)
  compileOnly(libs.waterfallApi)
}

tasks {
  shadowJar {
    platformRelocation("bungee", "xyz.jpenilla.minimotd.common")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
  }
  runWaterfall {
    waterfallVersion("1.20")
  }
}

bungee {
  name = rootProject.name
  main = "xyz.jpenilla.minimotd.bungee.MiniMOTDPlugin"
  author = "jmp"
}

modrinth {
  loaders.add("waterfall") // not sure why this isn't detected automatically from run-waterfall...
  gameVersions.addAll(minecraftVersion)
}
