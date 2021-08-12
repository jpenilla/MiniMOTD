import net.kyori.indra.git.IndraGitExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.lastCommitHash(): String =
  the<IndraGitExtension>().commit()?.name?.substring(0, 7) ?: "unknown"

val Project.libs: LibrariesForLibs
  get() = the()
