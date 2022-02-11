import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

apply<DokkaPlugin>()

fun createJavadocJarTask(dependingOn: AbstractDokkaTask) {
    tasks.create<Jar>("${dependingOn.name}Jar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dependingOn.outputDirectory)
        dependsOn(dependingOn)
    }
}

if (project == rootProject) {
    tasks.withType<DokkaMultiModuleTask>().all {
        createJavadocJarTask(dependingOn = this)
    }
} else {
    tasks.withType<DokkaTask>().all {
        createJavadocJarTask(dependingOn = this)
    }
}
