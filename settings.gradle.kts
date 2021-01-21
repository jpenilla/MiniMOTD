rootProject.name = "MiniMOTD"

setupSubproject("minimotd-common") {
  projectDir = file("common")
}
setupSubproject("minimotd-spigot") {
  projectDir = file("spigot")
}
/*setupSubproject("minimotd-sponge8") {
  projectDir = file("sponge8")
}*/
setupSubproject("minimotd-bungeecord") {
  projectDir = file("bungee")
}
setupSubproject("minimotd-velocity") {
  projectDir = file("velocity")
}
setupSubproject("minimotd-universal") {
  projectDir = file("universal")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
  include(name)
  project(":$name").apply(block)
}
