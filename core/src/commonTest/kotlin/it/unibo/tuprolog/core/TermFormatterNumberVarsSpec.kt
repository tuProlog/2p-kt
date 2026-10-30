package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class TermFormatterNumberVarsSpec :
    FunSpec({
        fun format(n: Int): String = TermFormatter.default().format(Struct.of("\$VAR", Integer.of(n)))

        test("numbered-variables-have-no-suffix-in-their-first-round") {
            assertEquals(
                listOf("A", "B", "Z", "A1", "B1", "Z1", "A2"),
                listOf(0, 1, 25, 26, 27, 51, 52).map(::format),
            )
        }
    })
