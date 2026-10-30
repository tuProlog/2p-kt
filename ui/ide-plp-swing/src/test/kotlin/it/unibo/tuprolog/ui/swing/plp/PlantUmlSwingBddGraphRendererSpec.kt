package it.unibo.tuprolog.ui.swing.plp

import io.kotest.core.spec.style.FunSpec
import javax.swing.JLabel
import javax.swing.JScrollPane
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PlantUmlSwingBddGraphRendererSpec :
    FunSpec({
        test("renders-valid-dot-as-an-actual-image-not-text") {
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

        test("garbage input still renders as a blank image rather than failing") {
            val component = PlantUmlSwingBddGraphRenderer.createGraphComponent("this is not valid dot {{{", "title")
            val scrollPane = assertIs<JScrollPane>(component)
            val label = assertIs<JLabel>(scrollPane.viewport.view)
            assertNotNull(label.icon)
        }
    })
