import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

/**
 * Registers a `swingE2eTest` source set and matching `swingE2eTest` [Test] task on a Swing UI module: a suite,
 * kept separate from `test`/`check`, that launches the module's real top-level application window and drives it
 * with AssertJ Swing (see `src/swingE2eTest/kotlin`).
 *
 * The new source set reuses this module's compiled `main` and `test` classes plus every dependency already on
 * `testImplementation`/`testRuntimeOnly` (so it automatically gets the same `kotlin("test")` setup as `test`),
 * and adds `assertj-swing` on top. The Kotlin Gradle plugin picks up `src/swingE2eTest/kotlin` for it by the same
 * naming convention it already uses for `main`/`test`, so no explicit source directory wiring is needed here.
 *
 * The resulting task genuinely drives Swing/AWT (`java.awt.Robot` included), so it needs a real or virtual
 * (Xvfb) display and must never run with `-Djava.awt.headless=true`; it is deliberately not wired into
 * `test`/`check`, so a display-less `./gradlew check` keeps working. Run it explicitly, e.g.
 * `xvfb-run -a ./gradlew :ide-swing:swingE2eTest` in CI.
 */
fun Project.registerSwingE2eTestSourceSet(assertjSwing: Any) {
    val sourceSets = extensions.getByType<JavaPluginExtension>().sourceSets
    val main = sourceSets.getByName("main")
    val test = sourceSets.getByName("test")
    val e2e =
        sourceSets.create("swingE2eTest") {
            compileClasspath += main.output + test.output
            runtimeClasspath += main.output + test.output
        }

    // The Java plugin already creates swingE2eTest{Implementation,RuntimeOnly,...} configurations as a
    // side effect of `sourceSets.create` above; just extend the matching `test` ones instead of re-creating them.
    configurations.getByName("swingE2eTestImplementation") { extendsFrom(configurations.getByName("testImplementation")) }
    configurations.getByName("swingE2eTestRuntimeOnly") { extendsFrom(configurations.getByName("testRuntimeOnly")) }
    dependencies.add("swingE2eTestImplementation", assertjSwing)

    val toolchains = extensions.getByType<JavaToolchainService>()
    tasks.register<Test>("swingE2eTest") {
        group = "verification"
        description = "Runs the full-application Swing end-to-end suite (AssertJ Swing); needs a real or " +
            "virtual (Xvfb) display -- never run with -Djava.awt.headless=true."
        testClassesDirs = e2e.output.classesDirs
        classpath = e2e.runtimeClasspath
        // AssertJ Swing 3.17.1's AWT hierarchy setup transitively touches java.applet.Applet, which some JDK
        // builds newer than the project's own JVM toolchain (see the `jvm` version in libs.versions.toml) have
        // finished removing (JEP 504); pin a known-good, older LTS runtime for this task specifically, rather
        // than whatever JDK happens to run the rest of the Gradle build.
        javaLauncher.set(toolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(21)) })
        // Third-party dialogs the suite drives (e.g. rstaui's Find/Replace/Go to line) are localized, and
        // button/label text would otherwise follow whatever locale the machine running the build happens to
        // have -- pin one so those lookups are deterministic on every developer machine and CI runner alike.
        systemProperty("user.language", "en")
        systemProperty("user.country", "US")
    }
}
