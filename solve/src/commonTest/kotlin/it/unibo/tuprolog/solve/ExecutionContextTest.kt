package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.solver.ExecutionContext
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [ExecutionContextImpl]
 *
 * @author Enrico
 */
internal class ExecutionContextTest {

    private val someLibraries = Libraries()
    private val someFlags = emptyMap<Atom, Term>()
    private val aStaticKB = ClauseDatabase.empty()
    private val aDynamicKB = ClauseDatabase.empty()
    private val aSubstitution = Substitution.empty()
    private val someSolverStrategies = object : SolverStrategies {
        override fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>, context: ExecutionContext): P = throw NotImplementedError()
        override fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>, context: ExecutionContext): C = throw NotImplementedError()
        override fun successCheckStrategy(term: Term, context: ExecutionContext): Boolean = throw NotImplementedError()
    }
    private val aScopedParentSequence = emptySequence<ExecutionContextImpl>()
    private val isChoicePointChild = true
    private val toCutContextParent = emptySequence<ExecutionContextImpl>()
    private val aParentRequestsSequence = emptySequence<Solve.Request<ExecutionContextImpl>>()
    private val aThrowRelatedContextParent = DummyInstances.executionContext

    @Test
    fun executionContextHoldsInsertedData() {
        val toBeTested = ExecutionContextImpl(someLibraries, someFlags, aStaticKB, aDynamicKB,
                aSubstitution, someSolverStrategies, aScopedParentSequence, isChoicePointChild,
                toCutContextParent, aParentRequestsSequence, aThrowRelatedContextParent)

        assertEquals(someLibraries, toBeTested.libraries)
        assertEquals(someFlags, toBeTested.flags)
        assertEquals(aStaticKB, toBeTested.staticKB)
        assertEquals(aDynamicKB, toBeTested.dynamicKB)
        assertEquals(aSubstitution, toBeTested.substitution)
        assertEquals(aScopedParentSequence, toBeTested.clauseScopedParents)
        assertEquals(someSolverStrategies, toBeTested.solverStrategies)
        assertEquals(isChoicePointChild, toBeTested.isChoicePointChild)
        assertEquals(toCutContextParent, toBeTested.toCutContextsParent)
        assertEquals(aParentRequestsSequence, toBeTested.logicalParentRequests)
        assertEquals(aThrowRelatedContextParent, toBeTested.throwRelatedToCutContextsParent)
    }

    @Test
    fun executionContextDefaultsCorrect() {
        val toBeTested = ExecutionContextImpl(someLibraries, someFlags, aStaticKB, aDynamicKB)

        assertEquals(Substitution.empty(), toBeTested.substitution)
        assertEquals(emptySequence(), toBeTested.clauseScopedParents)
        assertEquals(SolverStrategies.prologStandard, toBeTested.solverStrategies)
        assertEquals(false, toBeTested.isChoicePointChild)
        assertEquals(emptySequence(), toBeTested.toCutContextsParent)
        assertEquals(emptySequence(), toBeTested.logicalParentRequests)
        assertNull(toBeTested.throwRelatedToCutContextsParent)
    }

}
