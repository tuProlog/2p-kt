package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

class TermFormatterCanonicalSpec :
    FunSpec({
        // X and Y are the first variables with their names, so they share the per-name counter
        test("distinct-variables-get-distinct-names") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val formatted = TermFormatter.canonical().format(Struct.of("f", x, y, x))
            val names = formatted.removePrefix("f(").removeSuffix(")").split(", ")
            names shouldHaveSize 3
            names.forEach { it shouldStartWith "_" }
            names[0] shouldNotBe names[1]
            names[0] shouldBe names[2]
        }
    })
