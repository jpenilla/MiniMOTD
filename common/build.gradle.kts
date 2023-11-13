plugins {
  alias(libs.plugins.blossom)
  id("minimotd.base-conventions")
}

tasks.jar {
  from(rootProject.file("license.txt")) {
    rename { "license_${rootProject.name.lowercase()}.txt" }
  }
}

dependencies {
  api(libs.configurateHocon)
  api(platform(libs.adventureBom))
  api(libs.adventureApi)
  api(libs.adventureTextSerializerPlain)
  api(libs.adventureTextSerializerGson) {
    exclude("com.google.code.gson", "gson")
  }
  api(libs.minimessage)
  compileOnlyApi(libs.slf4jApi)
  compileOnlyApi(libs.checkerQual)
  compileOnlyApi(libs.gson)
  testImplementation(libs.gson)
  compileOnlyApi(libs.guava)
}

sourceSets.main {
  blossom {
    javaSources {
      property("PLUGIN_NAME", rootProject.name)
      property("PLUGIN_VERSION", project.version.toString())
      property("PLUGIN_WEBSITE", Constants.GITHUB_URL)
      property("GITHUB_USER", Constants.GITHUB_USER)
      property("GITHUB_REPO", Constants.GITHUB_REPO)
    }
  }
}
