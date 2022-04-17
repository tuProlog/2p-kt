import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

apply<DokkaPlugin>()

inline fun <reified A : Zip> createJavadocArchiveTask(dependingOn: AbstractDokkaTask): A {
    return tasks.create<A>("${dependingOn.name}${A::class.simpleName}") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dependingOn.outputDirectory)
        dependsOn(dependingOn)
    }
}

if (project == rootProject) {
    tasks.withType<DokkaMultiModuleTask>().all {
        createJavadocArchiveTask<Zip>(dependingOn = this)
    }
} else {
    tasks.withType<DokkaTask>().all {
        createJavadocArchiveTask<Jar>(dependingOn = this)
    }
}
