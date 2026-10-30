package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.test.assertEquals

/** Test class for [distinctSolutions], backing the `UniqueSolutions` flag's `on` setting. */
class DistinctSolutionsSpec :
    FunSpec({
        val scope = Scope.empty()

        val query = with(scope) { Struct.of("member", varOf("X"), Atom.of("list")) }

        fun exception() = ResolutionException(context = DummyInstances.executionContext)

        fun yesWith(value: Atom) = with(scope) { Solution.yes(query, Substitution.of(mapOf(varOf("X") to value))) }

        test("keeps-only-the-first-yes-for-each-distinct-solved-query") {
            val one = yesWith(Atom.of("1"))
            val two = yesWith(Atom.of("2"))
            val solutions = listOf(one, one, two, one)

            assertEquals(listOf(one, two), solutions.asSequence().distinctSolutions().toList())
        }

        test("never-drops-no-or-halt-solutions") {
            val one = yesWith(Atom.of("1"))
            val no = Solution.no(query)
            val halt = Solution.halt(query, exception())
            val solutions = listOf(one, one, no, one, halt, no)

            assertEquals(listOf(one, no, halt, no), solutions.asSequence().distinctSolutions().toList())
        }

        test("is-a-no-op-when-every-solved-query-is-already-distinct") {
            val solutions = listOf(yesWith(Atom.of("1")), yesWith(Atom.of("2")), yesWith(Atom.of("3")))

            assertEquals(solutions, solutions.asSequence().distinctSolutions().toList())
        }
    })
