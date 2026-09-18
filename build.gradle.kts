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
    jsProjects(":ide-web")
    // otherProjects(identifier, vararg other) *replaces* the whole set on every call (it's a setter, not an
    // accumulator - see kt-mpp's RootMultiProjectExtension), so these must be one call
    otherProjects(":documentation")

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
}

// each dependency here is a lazily-resolved cross-project task path (no evaluationDependsOn needed): every
// UI module with a runnable entry point registers its own "verifyFatJar" via buildSrc's
// registerVerifyFatJarTask (see that module's build.gradle.kts) -- this just aggregates them.
tasks.register("verifyFatJars") {
    group = "verification"
    description = "Runs every UI module's own verifyFatJar task."
    dependsOn(":repl:verifyFatJar", ":ide-swing:verifyFatJar", ":ide-plp-swing:verifyFatJar", ":full:verifyFatJar")
}
