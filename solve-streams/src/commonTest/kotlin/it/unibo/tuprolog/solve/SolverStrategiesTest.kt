package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.SolverStrategies.Companion.prologStandard
import it.unibo.tuprolog.solve.solver.orderWithStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [SolverStrategies.Companion]
 *
 * @author Enrico
 */
internal class SolverStrategiesTest {

    private val aContext = DummyInstances.executionContext

    private val predication = Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c")).argsSequence
    private val clauses = listOf(Fact.of(Atom.of("a")), Fact.of(Atom.of("b")), Fact.of(Atom.of("c"))).asSequence()

    @Test
    fun prologStandardPredicationChoiceStrategy() {
        val toBeTested = predication.orderWithStrategy(aContext, prologStandard::predicationChoiceStrategy)

        assertEquals(predication.toList(), toBeTested.toList())
    }

    @Test
    fun prologStandardClauseChoiceStrategy() {
        val toBeTested = clauses.orderWithStrategy(aContext, prologStandard::clauseChoiceStrategy)

        assertEquals(clauses.toList(), toBeTested.toList())
    }

    @Test
    fun prologStandardSuccessCheckStrategyConsidersOnlyTrue() {
        assertTrue { prologStandard.successCheckStrategy(Truth.`true`(), aContext) }
        assertFalse { prologStandard.successCheckStrategy(Atom.of("ciao"), aContext) }
    }

}
