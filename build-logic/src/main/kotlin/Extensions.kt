import net.kyori.indra.git.IndraGitExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

fun Project.lastCommitHash(): String =
  extensions.getByType(IndraGitExtension::class).commit()?.name?.substring(0, 7) ?: "unknown"

val Project.libs: LibrariesForLibs
  get() = the()
