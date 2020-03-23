package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Utils singleton to help testing [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal object StateEndUtils {

    /** The query to which test responses respond */
    internal val aQuery = Truth.ofTrue()

    /** The exception inside [anExceptionalResponse] */
    internal val anException = HaltException(context = DummyInstances.executionContext)

    internal val aYesResponse by lazy { Solve.Response(aQuery.yes()) }
    internal val aNoResponse by lazy { Solve.Response(aQuery.no()) }
    internal val anExceptionalResponse by lazy { Solve.Response(aQuery.halt(anException)) }

    internal val allResponseTypes by lazy { listOf(aYesResponse, aNoResponse, anExceptionalResponse) }


    internal val aSubstitution = Substitution.of("A", Truth.ofFalse())
    internal val someLibraries = Libraries(Library.of(alias = "stateEnd.test", operatorSet = OperatorSet.DEFAULT))
    internal val someFlags = mapOf(Atom.of("function1") to Atom.of("off"))
    internal val aStaticKB = ClauseDatabase.of({ factOf(atomOf("myStaticFact")) })
    internal val aDynamicKB = ClauseDatabase.of({ factOf(atomOf("myDynamicFact")) })

    internal val theRequestSideEffectManager = SideEffectManagerImpl()
    internal val aDifferentSideEffectManager =
        SideEffectManagerImpl(isChoicePointChild = true).also { assertNotEquals(it, theRequestSideEffectManager) }
    internal val theIntermediateStateRequest = createSolveRequest(aQuery).copy(
        context = ExecutionContextImpl(sideEffectManager = theRequestSideEffectManager)
    )
    internal val anIntermediateState = object : IntermediateState {
        override val solve: Solve.Request<ExecutionContext> = createSolveRequest(aQuery)
        override fun behave(): Sequence<State> = emptySequence()
        override val hasBehaved: Boolean = false
    }

    /** Utility function to assert that expected values are present inside the provided [actualStateEnd] */
    internal fun assertStateContentsCorrect(
        expectedLibraries: Libraries?,
        expectedFlags: PrologFlags?,
        expectedStaticKB: ClauseDatabase?,
        expectedDynamicKB: ClauseDatabase?,
        expectedSideEffectManager: SideEffectManager?,
        actualStateEnd: StateEnd
    ) = with(actualStateEnd.solve) {
        assertEquals(expectedLibraries, libraries)
        assertEquals(expectedFlags, flags)
        assertEquals(expectedStaticKB, staticKB)
        assertEquals(expectedDynamicKB, dynamicKB)
        assertEquals(expectedSideEffectManager, sideEffectManager)
    }
}
