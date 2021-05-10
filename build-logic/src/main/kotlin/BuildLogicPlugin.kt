import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildLogicPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.apply("java-library")
    target.plugins.apply("net.kyori.indra")
    target.plugins.apply("net.kyori.indra.git")
    target.plugins.apply("com.github.johnrengelman.shadow")
  }
}
