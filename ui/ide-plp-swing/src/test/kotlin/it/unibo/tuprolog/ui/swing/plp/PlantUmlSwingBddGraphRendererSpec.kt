package it.unibo.tuprolog.ui.swing.plp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import javax.swing.JLabel
import javax.swing.JScrollPane

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
            val scrollPane = component.shouldBeInstanceOf<JScrollPane>()
            val label = scrollPane.viewport.view.shouldBeInstanceOf<JLabel>()
            label.icon.shouldNotBeNull()
        }

        test("garbage input still renders as a blank image rather than failing") {
            val component = PlantUmlSwingBddGraphRenderer.createGraphComponent("this is not valid dot {{{", "title")
            val scrollPane = component.shouldBeInstanceOf<JScrollPane>()
            val label = scrollPane.viewport.view.shouldBeInstanceOf<JLabel>()
            label.icon.shouldNotBeNull()
        }
    })
