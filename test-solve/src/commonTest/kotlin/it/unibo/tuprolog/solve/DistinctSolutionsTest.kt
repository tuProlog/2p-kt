package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.test.Test
import kotlin.test.assertEquals

/** Test class for [distinctSolutions], backing the `UniqueSolutions` flag's `on` setting. */
internal class DistinctSolutionsTest {
    private val scope = Scope.empty()
    private val query = with(scope) { Struct.of("member", varOf("X"), Atom.of("list")) }
    private val exception get() = ResolutionException(context = DummyInstances.executionContext)

    private fun yesWith(value: Atom) = with(scope) { Solution.yes(query, Substitution.of(mapOf(varOf("X") to value))) }

    @Test
    fun keepsOnlyTheFirstYesForEachDistinctSolvedQuery() {
        val one = yesWith(Atom.of("1"))
        val two = yesWith(Atom.of("2"))
        val solutions = listOf(one, one, two, one)

        assertEquals(listOf(one, two), solutions.asSequence().distinctSolutions().toList())
    }

    @Test
    fun neverDropsNoOrHaltSolutions() {
        val one = yesWith(Atom.of("1"))
        val no = Solution.no(query)
        val halt = Solution.halt(query, exception)
        val solutions = listOf(one, one, no, one, halt, no)

        assertEquals(listOf(one, no, halt, no), solutions.asSequence().distinctSolutions().toList())
    }

    @Test
    fun isANoOpWhenEverySolvedQueryIsAlreadyDistinct() {
        val solutions = listOf(yesWith(Atom.of("1")), yesWith(Atom.of("2")), yesWith(Atom.of("3")))

        assertEquals(solutions, solutions.asSequence().distinctSolutions().toList())
    }
}
