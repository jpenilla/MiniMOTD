import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.AbstractArchiveTask

interface MiniMOTDPlatformExtension {
  val jarTask: Property<AbstractArchiveTask>
  val hangar: Property<Boolean>
}
