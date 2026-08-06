plugins {
  id("minimotd.shadow-platform")
  id("io.fand.plugin") version "latest.release"
}

dependencies {
  implementation(projects.minimotdCommon)
  compileOnly(libs.slf4jApi)
}

fandPlugin {
  id.set("minimotd")
  version.set(project.version.toString())
  mainClass.set("xyz.jpenilla.minimotd.fand.MiniMOTDFand")
  description.set(project.description ?: "")
  website.set(Constants.GITHUB_URL)
  license.set("MIT")
  apiVersion.set("0.1.1")
  authors.add("jmp")
  directRunGuard.set(false)
}

tasks {
  named("sourcesJar") {
    dependsOn("generateFandPluginDescriptor")
  }
  shadowJar {
    dependencies {
      exclude { dependency -> dependency.moduleGroup == "io.fand" }
      exclude { dependency -> dependency.moduleGroup == "net.kyori" }
      exclude { dependency -> dependency.moduleGroup == "org.slf4j" }
      exclude { dependency -> dependency.moduleGroup == "org.jspecify" }
      exclude { dependency -> dependency.moduleGroup == "com.google.code.gson" }
    }
  }
}
