import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import java.io.File

private const val LOGO_PNG_SIZE = 512f

private fun jsonField(
    json: String,
    name: String,
): String {
    val key = "\"$name\""
    val from = json.indexOf(key).also { check(it >= 0) { "missing '$name' in .img/logo.json" } } + key.length
    val value = json.substring(from).substringAfter(':').substringBefore(',').substringBefore('}').trim()
    return value.removeSurrounding("\"")
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
 * (`.img/logo.json` plus the matching SVG under `.img/logos`) to a single square PNG, so ide-* modules that need
 * a raster app icon/favicon don't each redo the conversion on their own. Uses Batik's `PNGTranscoder` in-process
 * (rather than shelling out to the `inkscape` CLI) so it needs no external tool and no `PATH`/install-location
 * lookup, both of which caused real failures when a Gradle daemon started outside a shell.
 */
private fun Project.ideLogoPngTask(): TaskProvider<Task> {
    val root = rootProject
    return if ("generateIdeLogoPng" in root.tasks.names) {
        root.tasks.named("generateIdeLogoPng")
    } else {
        root.tasks.register("generateIdeLogoPng") {
            val svg = defaultLogoSvg(root.projectDir)
            val outputFile = root.layout.buildDirectory.file("generated-logo/logo.png").get().asFile
            inputs.file(svg)
            inputs.file(root.projectDir.resolve(".img/logo.json"))
            outputs.file(outputFile)
            doLast {
                outputFile.parentFile.mkdirs()
                val transcoder =
                    PNGTranscoder().apply {
                        addTranscodingHint(PNGTranscoder.KEY_WIDTH, LOGO_PNG_SIZE)
                        addTranscodingHint(PNGTranscoder.KEY_HEIGHT, LOGO_PNG_SIZE)
                    }
                svg.inputStream().use { input ->
                    outputFile.outputStream().use { output ->
                        transcoder.transcode(TranscoderInput(input), TranscoderOutput(output))
                    }
                }
            }
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
