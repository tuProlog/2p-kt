package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

/*
 * Mutable theories follow the logical update view: a lookup or an iteration sees the clauses as of when it
 * started, whatever happens to the theory while it is consumed. Sizes cover both scanned and indexed procedures.
 */

private fun p(
    i: Int,
    tag: String = "a",
): Clause = Fact.of(Struct.of("p", Integer.of(i), Atom.of(tag)))

private val anyP: Clause = Fact.of(Struct.of("p", Var.anonymous(), Var.anonymous()))

private fun boundP(i: Int): Clause = Fact.of(Struct.of("p", Integer.of(i), Var.anonymous()))

private class SizeCase(
    val id: String,
    val size: Int,
)

private val sizes = listOf(SizeCase("scanned", 3), SizeCase("indexed", 50))

private fun theoryOf(size: Int): MutableTheory = MutableTheory.of(Unificator.default, List(size) { p(it) })

class LogicalUpdateViewSpec :
    FunSpec({
        withTests(nameFn = { "get-ignores-later-assertions/${it.id}" }, sizes) { case ->
            val theory = theoryOf(case.size)
            val seen = mutableListOf<Clause>()
            for (clause in theory[anyP]) {
                seen.add(clause)
                theory.assertZ(p(case.size + seen.size))
                theory.assertA(p(-seen.size))
            }
            seen shouldBe List(case.size) { p(it) }
            theory.size shouldBe 3L * case.size
        }

        withTests(nameFn = { "get-still-yields-retracted-clauses/${it.id}" }, sizes) { case ->
            val theory = theoryOf(case.size)
            val seen = mutableListOf<Clause>()
            for (clause in theory[anyP]) {
                seen.add(clause)
                theory.retractAll(anyP)
            }
            seen shouldBe List(case.size) { p(it) }
            theory.size shouldBe 0L
        }

        withTests(nameFn = { "indexed-get-ignores-later-assertions/${it.id}" }, sizes) { case ->
            val theory = theoryOf(case.size)
            val seen = theory[boundP(1)].onEach { theory.assertZ(p(1, "b")) }.toList()
            seen shouldBe listOf(p(1))
            theory[boundP(1)].toList() shouldBe listOf(p(1), p(1, "b"))
        }

        withTests(nameFn = { "clauses-ignore-updates-while-iterating/${it.id}" }, sizes) { case ->
            val theory = theoryOf(case.size)
            val seen = theory.clauses.map { it.also { clause -> theory.retract(clause) } }
            seen shouldBe List(case.size) { p(it) }
            theory.clauses.toList() shouldBe emptyList()
        }

        withTests(nameFn = { "new-lookups-see-updates/${it.id}" }, sizes) { case ->
            val theory = theoryOf(case.size)
            repeat(case.size * 3) { i ->
                theory.retract(boundP(i))
                theory.assertA(p(i, "b"))
            }
            theory[anyP].toList() shouldBe List(3 * case.size) { p(3 * case.size - 1 - it, "b") }
        }

        test("directives-follow-the-update-view") {
            val directive = { i: Int -> Directive.of(Struct.of("init", Integer.of(i))) }
            val theory = MutableTheory.of(Unificator.default, List(5) { directive(it) })
            val anyDirective = Directive.of(Var.anonymous())
            val seen = theory[anyDirective].onEach { theory.retract(it) }.toList()
            seen shouldBe List(5) { directive(it) }
            theory.directives.toList() shouldBe emptyList()
        }

        test("naive-unificator-lookups-follow-the-update-view") {
            val theory = MutableTheory.of(Unificator.naive(), List(10) { p(it) })
            val realOne = Fact.of(Struct.of("p", Real.of(1.0), Atom.of("a")))
            val seen = theory[realOne].onEach { theory.assertZ(p(1, "a")) }.toList()
            seen shouldBe listOf(p(1))
            theory[realOne].toList() shouldBe listOf(p(1), p(1))
        }

        test("lookups-stay-correct-across-compactions") {
            val theory = theoryOf(100)
            val pending = theory[anyP]
            repeat(90) { theory.retract(boundP(it)) }
            theory.size shouldBe 10L
            theory[boundP(95)].toList() shouldBe listOf(p(95))
            theory[boundP(5)].toList() shouldBe emptyList()
            theory.clauses.toList() shouldBe List(10) { p(90 + it) }
            pending.toList() shouldBe List(100) { p(it) }
        }
    })
