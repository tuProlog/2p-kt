import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.kotlin.dsl.create

class MockServicePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val start = project.task("startMock") { group = "mock" }
        val stop = project.task("stopMock") { group = "mock" }
        stop.dependsOn(start)
        listOf(start, stop).forEach {
            it.outputs.upToDateWhen { false }
        }
        val extension = project.extensions.create<MockServiceExtension>("mockService", start, stop)
        start.doFirst {
            extension.start()
            project.logger.log(LogLevel.LIFECYCLE, "Mock service listening on port ${extension.port}")
        }
        stop.doLast {
            extension.stop()
            project.logger.log(LogLevel.LIFECYCLE, "Mock service stopped")
        }
    }
}
