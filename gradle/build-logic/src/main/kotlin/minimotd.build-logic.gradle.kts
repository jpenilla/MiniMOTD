plugins {
  base
  id("io.papermc.hangar-publish-plugin")
}

fun jar(platform: String) = project(":minimotd-$platform")
  .extensions.getByType<MiniMOTDPlatformExtension>().jarTask.flatMap { it.archiveFile }

hangarPublish.publications.register("plugin") {
  version.set(project.version as String)
  id.set("MiniMOTD")
  channel.set("Release")
  changelog.set(releaseNotes)
  apiKey.set(providers.environmentVariable("HANGAR_UPLOAD_KEY"))
  platforms {
    paper {
      jar.set(jar("paper"))
      platformVersions = paperVersions
    }
    velocity {
      jar.set(jar("velocity"))
      platformVersions.addAll("3.4")
    }
    waterfall {
      jar.set(jar("bungeecord"))
      platformVersions.addAll("1.21")
    }
  }
}
