package it.unibo.tuprolog.ui.swing.plp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class DotToPlantUmlSpec :
    FunSpec({
        test("a BDD's DOT source converts to an equivalent PlantUML state diagram") {
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

            plantUml shouldBe
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
                """.trimIndent()
        }

        test("a quote in a node's label is turned into a single quote so PlantUML syntax stays valid") {
            val plantUml = dotToPlantUml("""1 [shape=record, label="p("a")"]""")
            plantUml.contains("""state "p('a')" as s1""").shouldBeTrue()
        }

        test("negative node ids (a negative hashCode) are turned into valid PlantUML state names") {
            val plantUml = dotToPlantUml("""-5 [shape=circle, label="0"]""")
            plantUml.contains("as sN5").shouldBeTrue()
        }

        test("unrecognized lines such as the surrounding digraph braces are silently skipped") {
            val plantUml = dotToPlantUml("digraph {\n}")
            plantUml shouldBe "@startuml\n!pragma layout smetana\nhide empty description\n@enduml"
        }
    })
