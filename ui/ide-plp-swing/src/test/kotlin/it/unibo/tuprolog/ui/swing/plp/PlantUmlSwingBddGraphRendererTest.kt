package it.unibo.tuprolog.ui.swing.plp

import javax.swing.JLabel
import javax.swing.JScrollPane
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PlantUmlSwingBddGraphRendererTest {
    @Test
    fun rendersValidDotAsAnActualImageNotText() {
        val dot =
            """
            digraph  {
            1237 [shape=circle, label="0"]
            1231 [shape=circle, label="1"]
            42 [shape=record, label="X"]
            42 -> 1237 [style=dashed]
            42 -> 1231
            }
            """.trimIndent()
        val component = PlantUmlSwingBddGraphRenderer.createGraphComponent(dot, "title")
        val scrollPane = assertIs<JScrollPane>(component)
        val label = assertIs<JLabel>(scrollPane.viewport.view)
        assertNotNull(label.icon)
    }

    @Test
    fun `garbage input still renders as a blank image rather than failing`() {
        val component = PlantUmlSwingBddGraphRenderer.createGraphComponent("this is not valid dot {{{", "title")
        val scrollPane = assertIs<JScrollPane>(component)
        val label = assertIs<JLabel>(scrollPane.viewport.view)
        assertNotNull(label.icon)
    }
}
