import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version Versions.com_github_johnrengelman_shadow_gradle_plugin
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.clikt_multiplatform)
                api(project(":core"))
                api(project(":solve-classic"))
                api(project(":parser-theory"))
            }
        }
    }
}

val githubToken: String? by project
val mainClass = "it.unibo.tuprolog.ui.repl.Main"

val shadowJar by tasks.creating(ShadowJar::class.java) {
    dependsOn("jvmMainClasses")
    archiveClassifier.set("redist")
    configurations = listOf(
        kotlin.jvm().compilations.getByName("main").compileDependencyFiles as Configuration
    )
    from(kotlin.jvm().compilations.getByName("main").output)
    manifest {
        attributes("Main-Class" to mainClass)
    }
}

if (githubToken != null) {
    rootProject.configure<GithubReleaseExtension> {
        releaseAssets(*(releaseAssets.toList() + shadowJar).toTypedArray())
    }

    rootProject.tasks.withType(GithubReleaseTask::class) {
        dependsOn(shadowJar)
    }
}