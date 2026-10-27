import io.github.gciatto.kt.mpp.Plugins
import io.github.gciatto.kt.mpp.helpers.ProjectType
import io.github.gciatto.kt.mpp.utils.log
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotest) apply false
    alias(libs.plugins.kover)
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

// Kotest (#1017): every Kotlin Multiplatform module with test sources (JS-only ones included) gets the Kotest
// engine and assertions, plus the KSP-based Kotest Gradle plugin that Kotlin/JS needs to discover specs (JVM
// discovers them on the JUnit Platform, enabled by `jvmTestFramework=junit5` in gradle.properties).
subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        if (file("src").listFiles().orEmpty().none { it.isDirectory && it.name.endsWith("Test") }) return@withId
        apply(
            plugin =
                rootProject.libs.plugins.ksp
                    .get()
                    .pluginId,
        )
        apply(
            plugin =
                rootProject.libs.plugins.kotest
                    .get()
                    .pluginId,
        )
        configure<KotlinMultiplatformExtension> {
            sourceSets.named("commonTest") {
                dependencies {
                    implementation(rootProject.libs.kotest.framework.engine)
                    implementation(rootProject.libs.kotest.assertions.core)
                }
            }
            sourceSets.matching { it.name == "jvmTest" }.configureEach {
                dependencies {
                    implementation(rootProject.libs.kotest.runner.junit5)
                }
            }
        }
    }
    // JVM-only modules (examples, ide-swing, ide-plp-swing) only need the JUnit Platform runner
    plugins.withId("org.jetbrains.kotlin.jvm") {
        dependencies {
            "testImplementation"(rootProject.libs.kotest.runner.junit5)
            "testImplementation"(rootProject.libs.kotest.assertions.core)
        }
    }
}

// Kotest (#1017): Kotlin tests must not regress to kotlin.test/JUnit runners, nor call runTest inside a leaf (not
// awaited on Kotlin/JS: use `test(...).config(coroutineTestScope = true)`). detekt's ForbiddenImport can't do this
// here: kt-mpp's detekt tasks never scan commonMain/commonTest, where most tests live. Java tests stay on JUnit.
val legacyTestImport =
    Regex(
        """^import (kotlin\.test\.(Test|Ignore|BeforeTest|AfterTest)|org\.junit\.|kotlinx\.coroutines\.test\.runTest)\b.*""",
        RegexOption.MULTILINE,
    )

subprojects {
    val verifyTestImports =
        tasks.register("verifyTestImports") {
            group = "verification"
            description = "Fails on kotlin.test/JUnit runner imports or runTest in Kotlin sources (#1017)."
            val sources = fileTree("src") { include("**/*.kt") }
            doLast {
                val offending =
                    sources.flatMap { file ->
                        legacyTestImport.findAll(file.readText()).map { "${file.relativeTo(rootDir)}: ${it.value}" }
                    }
                if (offending.isNotEmpty()) throw GradleException(offending.joinToString("\n", "Use Kotest instead:\n"))
            }
        }
    tasks.matching { it.name == "check" }.configureEach { dependsOn(verifyTestImports) }
}

// Coverage of the JVM test runs, aggregated at the root: `./gradlew koverHtmlReport` (or koverXmlReport).
// Docs and the fat-jar/examples aggregates are not production code; the Swing/web UIs are covered by
// display-bound E2E suites, which this report doesn't run. The test-only modules' own tests exercise production
// code (e.g. :solve's), so they are instrumented, but their own classes are left out of the report. (Excluding
// their source sets instead would also drop the coverage their tests record for other modules.)
val notCovered = setOf("documentation", "examples", "full", "ide-swing", "ide-plp-swing", "ide-web")

subprojects {
    if (name !in notCovered) {
        apply(plugin = "org.jetbrains.kotlinx.kover")
        rootProject.dependencies.add("kover", this)
    }
}

kover.reports.filters.excludes.projects
    .addAll(":test-dsl", ":test-solve")

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
