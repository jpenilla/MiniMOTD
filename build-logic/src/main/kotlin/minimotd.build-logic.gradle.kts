import io.papermc.hangarpublishplugin.model.Platforms

plugins {
  base
  id("io.papermc.hangar-publish-plugin")
}

fun jar(platform: String) = project(":minimotd-$platform")
  .the<MiniMOTDPlatformExtension>().jarTask.flatMap { it.archiveFile }

hangarPublish.publications.register("plugin") {
  version.set(project.version as String)
  namespace("jmp", "MiniMOTD")
  channel.set("Release")
  changelog.set(releaseNotes)
  apiKey.set(providers.environmentVariable("HANGAR_UPLOAD_KEY"))
  platforms {
    register(Platforms.PAPER) {
      jar.set(jar("bukkit"))
      val vers = bukkitVersions.toMutableList()
      vers -= "1.8.8"
      vers -= "1.8.9"
      vers += "1.8"
      platformVersions.addAll(vers)
    }
    register(Platforms.VELOCITY) {
      jar.set(jar("velocity"))
      platformVersions.addAll("3.2")
    }
    register(Platforms.WATERFALL) {
      jar.set(jar("bungeecord"))
      platformVersions.addAll("1.20")
    }
  }
}
