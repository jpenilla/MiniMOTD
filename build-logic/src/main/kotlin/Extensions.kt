import net.kyori.indra.git.IndraGitExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

fun Project.lastCommitHash(): String =
  extensions.getByType(IndraGitExtension::class).commit()?.name?.substring(0, 7) ?: "unknown"
