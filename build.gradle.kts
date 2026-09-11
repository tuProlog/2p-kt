import io.github.gciatto.kt.mpp.Plugins
import io.github.gciatto.kt.mpp.helpers.ProjectType
import io.github.gciatto.kt.mpp.utils.log
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile

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

/**
 * One UI module's fat jar, and how to check it actually works: [expectedEntryPoint] must match both the jar's
 * `Main-Class` manifest attribute and an actual class file inside the jar (catching a manifest/content
 * mismatch), and `java -jar` (forced headless, so the Swing frontends' own guard fires predictably instead of
 * opening a real window) must behave as declared by [expectSuccess]/[expectedOutputFragment] (successful CLIs)
 * or [expectedFailureFragment] (Swing entry points, which are expected to fail fast on their own headless check).
 */
data class FatJarCheck(
    val projectPath: String,
    val expectedEntryPoint: String,
    val launchArgs: List<String> = emptyList(),
    val expectSuccess: Boolean = true,
    val expectedOutputFragment: String? = null,
    val expectedFailureFragment: String? = null,
)

val fatJarChecks =
    listOf(
        FatJarCheck(
            ":repl",
            "it.unibo.tuprolog.ui.repl.Main",
            launchArgs = listOf("solve", "true."),
            expectedOutputFragment = "yes",
        ),
        FatJarCheck(
            ":full",
            "it.unibo.tuprolog.PrologCLI",
            launchArgs = listOf("solve", "true."),
            expectedOutputFragment = "yes",
        ),
        FatJarCheck(
            ":ide-swing",
            "it.unibo.tuprolog.ui.swing.Main",
            expectSuccess = false,
            expectedFailureFragment = "Cannot show the Swing IDE in a headless environment",
        ),
        FatJarCheck(
            ":ide-plp-swing",
            "it.unibo.tuprolog.ui.swing.plp.Main",
            expectSuccess = false,
            expectedFailureFragment = "Cannot show the Swing IDE in a headless environment",
        ),
    )

val minFatJarClassEntries = 200
val fatJarLaunchTimeoutSeconds = 30L

// needed to reference each project's `shadowJar` task below, before that project's own build script would
// otherwise have been evaluated
fatJarChecks.forEach { evaluationDependsOn(it.projectPath) }

tasks.register("verifyFatJars") {
    group = "verification"
    description = "Builds every UI module's fat jar (see allShadowJars) and checks its naming, manifest " +
        "entry point, contents, and that `java -jar` actually runs it as expected."

    val jarFiles =
        fatJarChecks.associate { check ->
            check.projectPath to
                project(check.projectPath).tasks.named("shadowJar").map { it.outputs.files.singleFile }
        }
    val projectVersions = fatJarChecks.associate { it.projectPath to project(it.projectPath).version.toString() }
    fatJarChecks.forEach { check ->
        dependsOn(project(check.projectPath).tasks.named("allShadowJars"))
    }

    doLast {
        fatJarChecks.forEach { check ->
            val projectName = check.projectPath.removePrefix(":")
            val jarFile = jarFiles.getValue(check.projectPath).get()
            logger.lifecycle("Verifying ${check.projectPath}'s fat jar: ${jarFile.name}")

            // 1. one fat jar per project, named "2p-PROJECTNAME-VERSION-redist.jar"
            val expectedName = "2p-$projectName-${projectVersions.getValue(check.projectPath)}-redist.jar"
            check(jarFile.name == expectedName) {
                "${check.projectPath}: expected fat jar named '$expectedName', got '${jarFile.name}'"
            }

            JarFile(jarFile).use { jar ->
                // 3. correct entry point in the manifest
                val manifestMainClass = jar.manifest.mainAttributes.getValue("Main-Class")
                check(manifestMainClass == check.expectedEntryPoint) {
                    "${check.projectPath}: expected Main-Class '${check.expectedEntryPoint}', got " +
                        "'$manifestMainClass'"
                }
                // 2. correct packages in there: the entry point class is actually present as a real
                // class file (not just declared in the manifest), and the jar is genuinely fat (bundled
                // dependency classes), not just the project's own thin classes
                val entryClassPath = "${check.expectedEntryPoint.replace('.', '/')}.class"
                check(jar.getJarEntry(entryClassPath) != null) {
                    "${check.projectPath}: entry point class '$entryClassPath' not found inside the jar"
                }
                val classEntryCount = jar.entries().asSequence().count { it.name.endsWith(".class") }
                check(classEntryCount > minFatJarClassEntries) {
                    "${check.projectPath}: only $classEntryCount .class entries in the jar, doesn't look " +
                        "like a fat jar with bundled dependencies (expected more than $minFatJarClassEntries)"
                }
            }

            // 4. running the fat jar with `java -jar` works
            val process =
                ProcessBuilder(
                    listOf("java", "-Djava.awt.headless=true", "-jar", jarFile.absolutePath) + check.launchArgs,
                ).redirectErrorStream(false).start()
            process.outputStream.close()
            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val finished = process.waitFor(fatJarLaunchTimeoutSeconds, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                error(
                    "${check.projectPath}: `java -jar ${jarFile.name}` did not terminate within " +
                        "$fatJarLaunchTimeoutSeconds s",
                )
            }
            val exitCode = process.exitValue()
            if (check.expectSuccess) {
                check(exitCode == 0) {
                    "${check.projectPath}: `java -jar ${jarFile.name}` exited with $exitCode, expected 0.\n" +
                        "stdout:\n$stdout\nstderr:\n$stderr"
                }
                check(check.expectedOutputFragment == null || check.expectedOutputFragment in stdout.lowercase()) {
                    "${check.projectPath}: expected stdout to contain '${check.expectedOutputFragment}'.\n" +
                        "stdout:\n$stdout"
                }
            } else {
                check(exitCode != 0) {
                    "${check.projectPath}: `java -jar ${jarFile.name}` unexpectedly succeeded (exit 0)"
                }
                check(check.expectedFailureFragment == null || check.expectedFailureFragment in stderr) {
                    "${check.projectPath}: expected stderr to contain '${check.expectedFailureFragment}'.\n" +
                        "stderr:\n$stderr"
                }
            }
            logger.lifecycle("  OK (${jarFile.length() / 1024} KiB, Main-Class=${check.expectedEntryPoint})")
        }
    }
}
