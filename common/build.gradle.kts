plugins {
  id("net.kyori.blossom")
}

dependencies {
  api("org.slf4j", "slf4j-api", "1.7.30")
  api("org.spongepowered", "configurate-hocon", "4.1.0-SNAPSHOT")
  platform(api("net.kyori", "adventure-bom", "4.7.0"))
  api("net.kyori", "adventure-text-serializer-gson")
  api("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT")
  compileOnlyApi("com.google.code.gson", "gson", "2.8.0")
  compileOnlyApi("com.google.guava", "guava", "21.0")
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
