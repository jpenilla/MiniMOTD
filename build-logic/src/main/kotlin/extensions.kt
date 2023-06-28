import net.kyori.indra.git.IndraGitExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the

fun Project.lastCommitHash(): String =
  the<IndraGitExtension>().commit()?.name?.substring(0, 7) ?: "unknown"

val Project.libs: LibrariesForLibs
  get() = the()

val Project.releaseNotes: Provider<String>
  get() = providers.environmentVariable("RELEASE_NOTES")

val Project.minecraftVersion
  get() = libs.versions.minecraft.get()

val bukkitVersions = listOf(
  "1.8.8",
  "1.8.9",
  "1.9.4",
  "1.10.2",
  "1.11.2",
  "1.12.2",
  "1.13.2",
  "1.14.4",
  "1.15.2",
  "1.16.5",
  "1.17.1",
  "1.18.2",
  "1.19.4",
  "1.20.1"
)
