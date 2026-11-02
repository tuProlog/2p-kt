package it.unibo.tuprolog.core.visitors

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import kotlin.test.assertEquals

class AbstractTermVisitorSpec :
    FunSpec({
        test("default-visitor-of/falls-back-to-the-default-value") {
            assertEquals("d:a", Atom.of("a").accept(DefaultTermVisitor.of { "d:$it" }))
        }

        test("exhaustive-visitor-of/falls-back-to-the-default-value") {
            assertEquals("e:a", Atom.of("a").accept(ExhaustiveTermVisitor.of { "e:$it" }))
        }

        test("term-visitor/indicators-and-clauses-are-structs") {
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "term"

                    override fun visitStruct(term: Struct) = "struct"
                }
            assertEquals("struct", Indicator.of("f", 1).accept(visitor))
            assertEquals("struct", Rule.of(Atom.of("h"), Atom.of("b")).accept(visitor))
        }
    })
