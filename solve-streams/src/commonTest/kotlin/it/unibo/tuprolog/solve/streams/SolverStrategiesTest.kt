package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.streams.SolverStrategies.Companion.prologStandard
import it.unibo.tuprolog.solve.streams.solver.orderWithStrategy
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

    private val predication = logicProgramming { "a" and "b" and "c" }.argsSequence
    private val clauses = logicProgramming { listOf(fact { "a" }, fact { "b" }, fact { "c" }) }.asSequence()

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
        assertTrue { prologStandard.successCheckStrategy(Truth.TRUE, aContext) }
        assertFalse { prologStandard.successCheckStrategy(Atom.of("ciao"), aContext) }
    }
}
