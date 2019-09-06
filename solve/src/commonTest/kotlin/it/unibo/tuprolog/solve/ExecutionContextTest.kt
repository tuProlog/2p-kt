package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [ExecutionContext]
 *
 * @author Enrico
 */
internal class ExecutionContextTest {

    private val someLibraries = Libraries()
    private val someFlags = emptyMap<Atom, Term>()
    private val aStaticKB = ClauseDatabase.of()
    private val aDynamicKB = ClauseDatabase.of()
    private val aComputationStartTime = 0L
    private val aSubstitution = Substitution.empty()
    private val someSolverStrategies = object : SolverStrategies {
        override fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>, context: ExecutionContext): P = throw NotImplementedError()
        override fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>, context: ExecutionContext): C = throw NotImplementedError()
        override fun successCheckStrategy(term: Term, context: ExecutionContext): Boolean = throw NotImplementedError()
    }
    private val aScopedParentSequence = emptySequence<ExecutionContext>()
    private val isChoicePointChild = true
    private val toCutContextParent = emptySequence<ExecutionContext>()
    private val aParentRequestsSequence = emptySequence<Solve.Request>()

    @Test
    fun executionContextHoldsInsertedData() {
        val toBeTested = ExecutionContext(someLibraries, someFlags, aStaticKB, aDynamicKB, aComputationStartTime,
                aSubstitution, someSolverStrategies, aScopedParentSequence, isChoicePointChild, toCutContextParent, aParentRequestsSequence)

        assertEquals(someLibraries, toBeTested.libraries)
        assertEquals(someFlags, toBeTested.flags)
        assertEquals(aStaticKB, toBeTested.staticKB)
        assertEquals(aDynamicKB, toBeTested.dynamicKB)
        assertEquals(aComputationStartTime, toBeTested.computationStartTime)
        assertEquals(aSubstitution, toBeTested.currentSubstitution)
        assertEquals(aScopedParentSequence, toBeTested.clauseScopedParents)
        assertEquals(someSolverStrategies, toBeTested.solverStrategies)
        assertEquals(isChoicePointChild, toBeTested.isChoicePointChild)
        assertEquals(toCutContextParent, toBeTested.toCutContextsParent)
        assertEquals(aParentRequestsSequence, toBeTested.parentRequests)
    }

    @Test
    fun executionContextDefaultsCorrect() {
        val toBeTested = ExecutionContext(someLibraries, someFlags, aStaticKB, aDynamicKB)

        assertTrue { currentTime() >= toBeTested.computationStartTime }
        assertEquals(Substitution.empty(), toBeTested.currentSubstitution)
        assertEquals(emptySequence(), toBeTested.clauseScopedParents)
        assertEquals(SolverStrategies.prologStandard, toBeTested.solverStrategies)
        assertEquals(false, toBeTested.isChoicePointChild)
        assertEquals(emptySequence(), toBeTested.toCutContextsParent)
        assertEquals(emptySequence(), toBeTested.parentRequests)
    }

}
