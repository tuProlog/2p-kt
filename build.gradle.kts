import io.github.gciatto.kt.mpp.Plugins
import io.github.gciatto.kt.mpp.helpers.ProjectType
import io.github.gciatto.kt.mpp.utils.log

buildscript {
    configurations.classpath {
        resolutionStrategy {
            // org.danilopianini:publish-on-central 9.2.5+ bumps its maven-central-portal-kotlin-api-jvm
            // dependency (a generated Ktor HTTP client) from v4 to v4.1.0+; that client's compiled bytecode
            // needs a kotlinx-coroutines Job.invokeOnCompletion$default shape that only exists in coroutines
            // >= 1.11.0. Gradle itself bundles kotlinx-coroutines-core 1.10.2 internally, and its classloader
            // always wins for kotlinx.coroutines.* symbols regardless of what THIS classpath resolves --
            // forcing a newer (or older) coroutines version here has no effect (verified locally: forcing
            // 1.10.2 or 1.11.0 explicitly produces the identical crash), so the only fix is staying on a
            // publish-on-central release whose bundled Ktor client was compiled against a coroutines API
            // shape 1.10.2 actually has. 9.2.4 is the newest such release (bisected locally: 9.2.4 works,
            // 9.2.5 crashes with `NoSuchMethodError: Job.invokeOnCompletion$default` on every subproject's
            // releaseMavenCentralPortalPublication, identically to what killed the 2.0.8 release attempt).
            // Remove this once Gradle bundles coroutines >= 1.11.0, or upstream re-tests against an
            // older/compatible Ktor: https://github.com/DanySK/publish-on-central/issues/1804
            force("org.danilopianini:publish-on-central:9.2.4")
        }
    }
}

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
