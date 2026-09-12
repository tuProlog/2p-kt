import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import java.io.File

private const val LOGO_PNG_SIZE = 512

private fun jsonField(
    json: String,
    name: String,
): String {
    val key = "\"$name\""
    val from = json.indexOf(key).also { check(it >= 0) { "missing '$name' in .img/logo.json" } } + key.length
    val value = json.substring(from).substringAfter(':').substringBefore(',').substringBefore('}').trim()
    return value.removeSurrounding("\"")
}

/**
 * Locates the `inkscape` executable. A bare `"inkscape"` relies on the *shell's* `PATH`, which a Gradle daemon
 * started from an IDE/launcher (as opposed to an interactive terminal sourcing `.zshrc`/`.bashrc`) often doesn't
 * inherit, so `Exec` fails with "problem occurred starting process" even though `inkscape` is installed. Check
 * well-known install locations first, then `PATH`, and only fall back to the bare name as a last resort.
 */
private fun resolveInkscapeExecutable(project: Project): String {
    project.findProperty("inkscape")?.toString()?.let { return it }
    System.getenv("INKSCAPE")?.let { return it }
    val exeName = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "inkscape.exe" else "inkscape"
    val candidateDirs =
        listOf(
            "/opt/homebrew/bin",
            "/usr/local/bin",
            "/usr/bin",
            "/Applications/Inkscape.app/Contents/MacOS",
            "C:\\Program Files\\Inkscape\\bin",
        ) + System.getenv("PATH").orEmpty().split(File.pathSeparatorChar)
    return candidateDirs
        .map { File(it, exeName) }
        .firstOrNull { it.canExecute() }
        ?.absolutePath
        ?: exeName
}

/** Resolves the SVG file for the default logo described by `.img/logo.json` (variant/tile/rounded flags). */
private fun defaultLogoSvg(rootDir: File): File {
    val json = rootDir.resolve(".img/logo.json").readText()
    val variant = jsonField(json, "variant")
    val tile = jsonField(json, "tile").toBoolean()
    val rounded = tile && jsonField(json, "rounded").toBoolean()
    val name = "2p-$variant" + (if (tile) "-tile" else "") + (if (rounded) "-rounded" else "")
    return rootDir.resolve(".img/logos/$name.svg").also {
        check(it.isFile) { "default logo SVG not found: $it (as configured by .img/logo.json)" }
    }
}

/**
 * Registers (once, on the root project, shared by every caller) a task rasterizing the current default logo
 * (`.img/logo.json` plus the matching SVG under `.img/logos`) to a single square PNG via the `inkscape` CLI,
 * so ide-* modules that need a raster app icon/favicon don't each shell out to inkscape on their own.
 */
private fun Project.ideLogoPngTask(): TaskProvider<Exec> {
    val root = rootProject
    return if ("generateIdeLogoPng" in root.tasks.names) {
        root.tasks.named<Exec>("generateIdeLogoPng")
    } else {
        root.tasks.register<Exec>("generateIdeLogoPng") {
            val svg = defaultLogoSvg(root.projectDir)
            val outputFile = root.layout.buildDirectory.file("generated-logo/logo.png").get().asFile
            inputs.file(svg)
            inputs.file(root.projectDir.resolve(".img/logo.json"))
            outputs.file(outputFile)
            doFirst { outputFile.parentFile.mkdirs() }
            executable = resolveInkscapeExecutable(project)
            args(
                svg.absolutePath,
                "--export-type=png",
                "--export-filename=${outputFile.absolutePath}",
                "-w",
                "$LOGO_PNG_SIZE",
                "-h",
                "$LOGO_PNG_SIZE",
            )
        }
    }
}

/**
 * Makes this module's [resourcesTaskName] task (e.g. `processResources`/`jsProcessResources`) include a
 * `logo.png` generated from the project's current default logo, so the module always ships the default icon
 * (see [ideLogoPngTask]) without having to track a static image file.
 */
fun Project.wireDefaultLogoResource(resourcesTaskName: String) {
    val generateLogo = ideLogoPngTask()
    val copyIdeLogo =
        tasks.register<Copy>("copyIdeLogo") {
            dependsOn(generateLogo)
            from(generateLogo.map { it.outputs.files.singleFile })
            into(layout.buildDirectory.dir("generated-resources/logo"))
        }
    tasks.named(resourcesTaskName) {
        dependsOn(copyIdeLogo)
        (this as Copy).from(layout.buildDirectory.dir("generated-resources/logo"))
    }
}
