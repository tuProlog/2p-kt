import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

apply<DokkaPlugin>()

tasks.withType<DokkaTask>().all {
    val dokkaHtml = this
    tasks.create<Jar>("${name}Jar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}
