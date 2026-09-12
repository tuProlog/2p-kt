package it.unibo.tuprolog.ui.swing.plp

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JScrollPane

/**
 * Renders a BDD's DOT source as an actual graph picture by converting it to an equivalent PlantUML state
 * diagram (see [dotToPlantUml]) and rendering that with PlantUML's own pure-JVM Smetana layout engine (the
 * `!pragma layout smetana` in [dotToPlantUml]'s output, same engine `:documentation`'s `generateDiagrams` task
 * already uses) - no native Graphviz install and, unlike the graphviz-java renderer this replaced, no GraalVM
 * JS engine either, which broke application startup entirely on JDKs newer than 21.
 */
object PlantUmlSwingBddGraphRenderer : SwingBddGraphRenderer {
    override fun createGraphComponent(
        dot: String,
        title: String?,
    ): JComponent =
        runCatching {
            val bytes =
                ByteArrayOutputStream()
                    .also {
                        SourceStringReader(dotToPlantUml(dot)).outputImage(it, FileFormatOption(FileFormat.PNG))
                    }.toByteArray()
            ImageIO.read(ByteArrayInputStream(bytes)) ?: error("PlantUML produced no image")
        }.fold(
            onSuccess = { image ->
                JScrollPane(
                    JLabel(ImageIcon(image)).apply {
                        name = "bddGraphImageLabel"
                        toolTipText = title
                    },
                )
            },
            onFailure = { failure ->
                // Falls back to the raw DOT text rather than an empty/broken panel, e.g. on a rendering-engine
                // failure; still shows something useful for diagnosing what went wrong, including the error
                // itself (surfaced as a tooltip, since the fallback view has no other room for it).
                DotTextSwingBddGraphRenderer.createGraphComponent(dot, title).apply {
                    toolTipText = "Could not render the graph picture: ${failure.message}"
                }
            },
        )
}
