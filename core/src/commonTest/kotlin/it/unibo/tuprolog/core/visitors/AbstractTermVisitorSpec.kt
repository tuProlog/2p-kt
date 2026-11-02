package it.unibo.tuprolog.core.visitors

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

class AbstractTermVisitorSpec :
    FunSpec({
        test("default-visitor-of/falls-back-to-the-default-value") {
            Atom.of("a").accept(DefaultTermVisitor.of { "d:$it" }) shouldBe "d:a"
        }

        test("exhaustive-visitor-of/falls-back-to-the-default-value") {
            Atom.of("a").accept(ExhaustiveTermVisitor.of { "e:$it" }) shouldBe "e:a"
        }

        test("term-visitor/indicators-and-clauses-are-structs") {
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "term"

                    override fun visitStruct(term: Struct) = "struct"
                }
            Indicator.of("f", 1).accept(visitor) shouldBe "struct"
            Rule.of(Atom.of("h"), Atom.of("b")).accept(visitor) shouldBe "struct"
        }
    })
