package it.unibo.tuprolog.ui.swing.plp

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.GraphvizException
import guru.nidi.graphviz.engine.GraphvizJdkEngine
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JScrollPane

/**
 * Renders a BDD's DOT source as an actual graph picture via graphviz-java, forced onto its JDK/GraalVM-JS engine
 * (see the `graphviz-js-engine` runtime dependency in build.gradle.kts) rather than the default engine
 * auto-detection, which would otherwise happily shell out to a native `dot` binary if one happens to be on
 * `PATH` - the same kind of environment-dependent fragility already fixed for the default logo (see
 * `buildSrc/IdeLogo.kt`), and one this pure-JVM engine needs no external install to avoid.
 */
object GraphvizSwingBddGraphRenderer : SwingBddGraphRenderer {
    init {
        Graphviz.useEngine(GraphvizJdkEngine())
    }

    override fun createGraphComponent(
        dot: String,
        title: String?,
    ): JComponent =
        try {
            val image = Graphviz.fromString(dot).render(Format.PNG).toImage()
            JScrollPane(
                JLabel(ImageIcon(image)).apply {
                    name = "bddGraphImageLabel"
                    toolTipText = title
                },
            )
        } catch (malformed: GraphvizException) {
            // Falls back to the raw DOT text rather than an empty/broken panel, e.g. on malformed DOT or a
            // rendering-engine failure; still shows something useful for diagnosing what went wrong, including
            // the error itself (surfaced as a tooltip, since the fallback view has no other room for it).
            DotTextSwingBddGraphRenderer.createGraphComponent(dot, title).apply {
                toolTipText = "Could not render the graph picture: ${malformed.message}"
            }
        }
}
