import io.github.gciatto.kt.mpp.Plugins
import io.github.gciatto.kt.mpp.helpers.ProjectType
import io.github.gciatto.kt.mpp.utils.log

plugins {
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.ktMpp.multiProjectHelper)
}

group = "it.unibo.tuprolog"

gitSemVer {
    buildMetadataSeparator.set("-")
}

log("version: $version", LogLevel.LIFECYCLE)

multiProjectHelper {
    defaultProjectType = ProjectType.KOTLIN

    jvmProjects(":examples", ":ide-swing", ":ide-plp-swing")
    // otherProjects(identifier, vararg other) *replaces* the whole set on every call (it's a setter, not an
    // accumulator - see kt-mpp's RootMultiProjectExtension), so these must be one call: two separate calls left
    // only ":documentation" in "otherProjects", silently dropping ":ide-web" back onto the default Kotlin
    // project template on top of its own explicit plugin block - which built successfully with no error, but
    // silently produced a broken production webpack bundle (its main() reduced to nothing at runtime).
    otherProjects(":ide-web", ":documentation")

    val baseProjectTemplate =
        buildSet {
            add(Plugins.documentation)
            add(Plugins.linter)
            add(Plugins.bugFinder)
            add(Plugins.versions)
        }

    ktProjectTemplate =
        buildSet {
            addAll(baseProjectTemplate)
            add(Plugins.multiplatform)
        }

    jvmProjectTemplate =
        buildSet {
            addAll(baseProjectTemplate)
            add(Plugins.jvmOnly)
        }

    jsProjectTemplate =
        buildSet {
            addAll(baseProjectTemplate)
            add(Plugins.jsOnly)
        }

    otherProjectTemplate =
        buildSet {
            add(Plugins.versions)
        }

    applyProjectTemplates()
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = libs.versions.jvm.get()
        parallel = true
    }
    if (project.findProperty("showTestsInConsole")?.toString()?.toBoolean() == true) {
        tasks.withType<Test>().configureEach {
            testLogging {
                events("passed", "skipped", "failed", "standardOut", "standardError")
                showStandardStreams = true
            }
        }
    }
}
