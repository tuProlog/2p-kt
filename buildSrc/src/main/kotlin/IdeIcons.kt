import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register

/**
 * Makes this module's [resourcesTaskName] task ship the shared action icons under `.img/ide-icons` (New, Open,
 * Save, Save-as, Solve, Solve-all, Clear - sourced from the classic tuProlog Swing IDE) under an `icons/`
 * classpath (or web root) folder, so ide-swing and ide-web can both load them by the same relative path.
 */
fun Project.wireIdeIconsResource(resourcesTaskName: String) {
    val copyIdeIcons =
        tasks.register<Copy>("copyIdeIcons") {
            from(rootProject.layout.projectDirectory.dir(".img/ide-icons"))
            into(layout.buildDirectory.dir("generated-resources/icons/icons"))
        }
    tasks.named(resourcesTaskName) {
        dependsOn(copyIdeIcons)
        (this as Copy).from(layout.buildDirectory.dir("generated-resources/icons"))
    }
}
