import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class FileCopyTask : DefaultTask() {
  @InputFile
  val fileToCopy = project.objects.fileProperty()

  @OutputDirectory
  val destinationDirectory = project.objects.directoryProperty()

  private val destination = project.objects.fileProperty().convention(destinationDirectory.file(fileToCopy.map { it.asFile.name }))

  @TaskAction
  private fun copyFile() {
    destinationDirectory.get().asFile.mkdirs()
    fileToCopy.get().asFile.copyTo(destination.get().asFile, overwrite = true)
  }
}
