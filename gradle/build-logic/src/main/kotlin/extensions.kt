import net.kyori.indra.git.IndraGitExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

fun Project.lastCommitHash(): String =
  extensions.getByType<IndraGitExtension>().commit()?.name?.substring(0, 7) ?: "unknown"

val Project.libs: LibrariesForLibs
  get() = extensions.getByType()

val Project.releaseNotes: Provider<String>
  get() = providers.environmentVariable("RELEASE_NOTES")

val Project.minecraftVersion: String
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
  "1.20.6",
  "1.21.7",
)

val productionJarAttribute = Attribute.of("minimotd.productionJar", String::class.java)
