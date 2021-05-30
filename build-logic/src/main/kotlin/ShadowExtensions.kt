import com.github.jengelman.gradle.plugins.shadow.internal.DependencyFilter
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun ShadowJar.commonRelocation(pkg: String) {
  relocate(pkg, "${Constants.RELOCATION_BASE_PACKAGE}.$pkg")
}

fun ShadowJar.platformRelocation(platform: String, pkg: String) {
  relocate(pkg, "${Constants.RELOCATION_BASE_PACKAGE}.platform_$platform.$pkg")
}

fun ShadowJar.configureForNativeAdventurePlatform() {
  platformRelocation("kyori", "net.kyori.adventure.text.minimessage")
  dependencies {
    excludeAdventureExceptMiniMessage()
  }
}

fun ShadowJar.commonConfiguration() {
  commonRelocation("org.spongepowered.configurate")
  commonRelocation("com.typesafe.config")
  dependencies {
    exclude(dependency("org.checkerframework:checker-qual"))
    exclude(dependency("org.jetbrains:annotations")) // transitive dep from adventure
  }
}

private fun DependencyFilter.excludeAdventureExceptMiniMessage() {
  exclude { dependency ->
    dependency.moduleGroup == "net.kyori"
      && dependency.moduleName != "adventure-text-minimessage"
  }
}
