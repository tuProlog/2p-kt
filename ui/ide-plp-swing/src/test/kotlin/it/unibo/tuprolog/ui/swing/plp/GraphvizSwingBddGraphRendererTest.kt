package it.unibo.tuprolog.ui.swing.plp

import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GraphvizSwingBddGraphRendererTest {
    @Test
    fun rendersValidDotAsAnActualImageNotText() {
        val component = GraphvizSwingBddGraphRenderer.createGraphComponent("digraph { a -> b; }", "title")
        val scrollPane = assertIs<JScrollPane>(component)
        val label = assertIs<JLabel>(scrollPane.viewport.view)
        assertNotNull(label.icon)
    }

    @Test
    fun fallsBackToDotTextOnMalformedDot() {
        val component = GraphvizSwingBddGraphRenderer.createGraphComponent("this is not valid dot {{{", "title")
        val scrollPane = assertIs<JScrollPane>(component)
        assertIs<JTextArea>(scrollPane.viewport.view)
    }
}
