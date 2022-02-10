import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

apply<DokkaPlugin>()

tasks.withType<DokkaMultiModuleTask>().all {
    val dokkaHtml = this
    tasks.create<Jar>("${name}Jar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}
