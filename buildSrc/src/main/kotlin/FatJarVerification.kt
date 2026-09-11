import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile

private const val MIN_FAT_JAR_CLASS_ENTRIES = 200
private const val FAT_JAR_LAUNCH_TIMEOUT_SECONDS = 30L

/**
 * Registers a `verifyFatJar` task on this project (which must already apply the `kt-mpp` `fatJar` plugin and
 * set `fatJarEntryPoint`) that builds its fat jar (via `allShadowJars`) and checks it end to end:
 * - naming: `2p-PROJECTNAME-VERSION-redist.jar`;
 * - [entryPoint] matches the jar's `Main-Class` manifest attribute *and* is actually present as a class file
 *   inside the jar (catching a manifest/content mismatch);
 * - the jar is genuinely fat (bundled dependency classes), not just the project's own thin classes;
 * - `java -jar` (forced headless, so a Swing frontend's own guard fires predictably instead of opening a real
 *   window) behaves as declared: on [expectSuccess], it must exit `0` and print [expectedOutputFragment] to
 *   stdout; otherwise it must exit non-zero with [expectedFailureFragment] in stderr.
 */
fun Project.registerVerifyFatJarTask(
    entryPoint: String,
    launchArgs: List<String> = emptyList(),
    expectSuccess: Boolean = true,
    expectedOutputFragment: String? = null,
    expectedFailureFragment: String? = null,
): TaskProvider<Task> =
    tasks.register("verifyFatJar") {
        group = "verification"
        description = "Builds this module's fat jar (see allShadowJars) and checks its naming, manifest entry " +
            "point, contents, and that `java -jar` actually runs it as expected."
        dependsOn(tasks.named("allShadowJars"))

        val jarFileProvider = tasks.named("shadowJar").map { it.outputs.files.singleFile }
        val projectName = project.name
        val projectVersion = project.version.toString()

        doLast {
            val jarFile = jarFileProvider.get()
            logger.lifecycle("Verifying fat jar: ${jarFile.name}")

            val expectedName = "2p-$projectName-$projectVersion-redist.jar"
            check(jarFile.name == expectedName) {
                "expected fat jar named '$expectedName', got '${jarFile.name}'"
            }

            JarFile(jarFile).use { jar ->
                val manifestMainClass = jar.manifest.mainAttributes.getValue("Main-Class")
                check(manifestMainClass == entryPoint) {
                    "expected Main-Class '$entryPoint', got '$manifestMainClass'"
                }
                val entryClassPath = "${entryPoint.replace('.', '/')}.class"
                check(jar.getJarEntry(entryClassPath) != null) {
                    "entry point class '$entryClassPath' not found inside the jar"
                }
                val classEntryCount = jar.entries().asSequence().count { it.name.endsWith(".class") }
                check(classEntryCount > MIN_FAT_JAR_CLASS_ENTRIES) {
                    "only $classEntryCount .class entries in the jar, doesn't look like a fat jar with bundled " +
                        "dependencies (expected more than $MIN_FAT_JAR_CLASS_ENTRIES)"
                }
            }

            val process =
                ProcessBuilder(listOf("java", "-Djava.awt.headless=true", "-jar", jarFile.absolutePath) + launchArgs)
                    .redirectErrorStream(false)
                    .start()
            process.outputStream.close()
            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val finished = process.waitFor(FAT_JAR_LAUNCH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                error("`java -jar ${jarFile.name}` did not terminate within $FAT_JAR_LAUNCH_TIMEOUT_SECONDS s")
            }
            val exitCode = process.exitValue()
            if (expectSuccess) {
                check(exitCode == 0) {
                    "`java -jar ${jarFile.name}` exited with $exitCode, expected 0.\n" +
                        "stdout:\n$stdout\nstderr:\n$stderr"
                }
                check(expectedOutputFragment == null || expectedOutputFragment in stdout.lowercase()) {
                    "expected stdout to contain '$expectedOutputFragment'.\nstdout:\n$stdout"
                }
            } else {
                check(exitCode != 0) {
                    "`java -jar ${jarFile.name}` unexpectedly succeeded (exit 0)"
                }
                check(expectedFailureFragment == null || expectedFailureFragment in stderr) {
                    "expected stderr to contain '$expectedFailureFragment'.\nstderr:\n$stderr"
                }
            }
            logger.lifecycle("  OK (${jarFile.length() / 1024} KiB, Main-Class=$entryPoint)")
        }
    }
