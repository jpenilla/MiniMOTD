import com.github.jengelman.gradle.plugins.shadow.internal.DependencyFilter
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

private const val RELOCATION_BASE_PACKAGE = "xyz.jpenilla.minimotd.lib"

fun ShadowJar.commonRelocation(pkg: String) {
  relocate(pkg, "$RELOCATION_BASE_PACKAGE.$pkg")
}

fun ShadowJar.platformRelocation(platform: String, pkg: String) {
  relocate(pkg, "$RELOCATION_BASE_PACKAGE.platform_$platform.$pkg")
}

fun ShadowJar.configureForNativeAdventurePlatform() {
  platformRelocation("kyori", "net.kyori.adventure.text.minimessage")
  dependencies {
    excludeAdventureExceptMiniMessage()
  }
}

private fun DependencyFilter.excludeAdventureExceptMiniMessage() {
  exclude { dependency ->
    dependency.moduleGroup == "net.kyori"
      && dependency.moduleName != "adventure-text-minimessage"
  }
}
