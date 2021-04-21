plugins {
  id("net.kyori.blossom")
}

tasks.jar {
  from(rootProject.file("license.txt")) {
    rename { "license_${rootProject.name.toLowerCase()}.txt" }
  }
}

dependencies {
  api(libs.slf4jApi)
  api(libs.configurateHocon)
  api(platform(libs.adventureBom))
  api(libs.adventureTextSerializerGson) {
    exclude("com.google.code.gson", "gson")
  }
  api(libs.minimessage)
  compileOnlyApi(libs.checkerQual)
  compileOnlyApi(libs.gson)
  testImplementation(libs.gson)
  compileOnlyApi(libs.guava)
}

blossom {
  fun replaceTokens(file: String, vararg tokens: Pair<String, String>) = tokens.forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
  replaceTokens(
    "src/main/java/xyz/jpenilla/minimotd/common/Constants.java",
    "PLUGIN_NAME" to rootProject.name,
    "PLUGIN_VERSION" to project.version.toString(),
    "PLUGIN_WEBSITE" to rootProject.ext["url"].toString()
  )
}
