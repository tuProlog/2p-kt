package it.unibo.tuprolog.ui.swing.plp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DotToPlantUmlTest {
    @Test
    fun `a BDD's DOT source converts to an equivalent PlantUML state diagram`() {
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

        val plantUml = dotToPlantUml(dot)

        assertEquals(
            """
            @startuml
            !pragma layout smetana
            hide empty description
            state "0" as s1237
            state "1" as s1231
            state "X" as s42
            s42 -[dashed]-> s1237
            s42 --> s1231
            @enduml
            """.trimIndent(),
            plantUml,
        )
    }

    @Test
    fun `a quote in a node's label is turned into a single quote so PlantUML syntax stays valid`() {
        val plantUml = dotToPlantUml("""1 [shape=record, label="p("a")"]""")
        assertTrue(plantUml.contains("""state "p('a')" as s1"""))
    }

    @Test
    fun `negative node ids (a negative hashCode) are turned into valid PlantUML state names`() {
        val plantUml = dotToPlantUml("""-5 [shape=circle, label="0"]""")
        assertTrue(plantUml.contains("as sN5"))
    }

    @Test
    fun `unrecognized lines such as the surrounding digraph braces are silently skipped`() {
        val plantUml = dotToPlantUml("digraph {\n}")
        assertEquals("@startuml\n!pragma layout smetana\nhide empty description\n@enduml", plantUml)
    }
}
