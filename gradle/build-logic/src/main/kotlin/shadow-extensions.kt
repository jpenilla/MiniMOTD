import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.DuplicatesStrategy

fun ShadowJar.commonRelocation(pkg: String) {
  relocate(pkg, "${Constants.RELOCATION_BASE_PACKAGE}.$pkg")
}

fun ShadowJar.commonConfiguration() {
  mergeServiceFiles()
  // Needed for mergeServiceFiles to work properly in Shadow 9+
  filesMatching("META-INF/services/**") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  commonRelocation("org.spongepowered.configurate")
  commonRelocation("com.typesafe.config")
  dependencies {
    exclude(dependency("org.checkerframework:checker-qual"))
    exclude(dependency("org.jetbrains:annotations")) // transitive dep from adventure
  }
}

fun ShadowJar.configureForNativeAdventurePlatform() {
  dependencies {
    exclude { dependency -> dependency.moduleGroup == "net.kyori" }
  }
}
