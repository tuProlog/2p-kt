package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

/*
 * Mutable theories follow the logical update view (#1019): a lookup or an iteration sees the clauses as of when it
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
    })
