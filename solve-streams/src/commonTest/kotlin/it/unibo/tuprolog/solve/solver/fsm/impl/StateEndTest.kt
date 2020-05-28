package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aDifferentSideEffectManager
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aDynamicKb
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aFullExceptionResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aFullNoResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aFullYesResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aMinimalExceptionResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aMinimalNoResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aMinimalYesResponse
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aQuery
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aStaticKb
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.aSubstitution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.allResponseTypes
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.anException
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.anIntermediateState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.assertStateContentsCorrect
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.defaultDynamicKb
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.defaultFlags
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.defaultLibraries
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.defaultStaticKb
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.someFlags
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.someLibraries
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.theIntermediateStateRequest
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateEndUtils.theRequestSideEffectManager
import it.unibo.tuprolog.theory.Theory
import kotlin.test.*

/**
 * Test class for [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal class StateEndTest {

    private val minimalResponseStateEndInstances = listOf(
        StateEnd.True(aMinimalYesResponse),
        StateEnd.False(aMinimalNoResponse),
        StateEnd.Halt(aMinimalExceptionResponse)
    )

    private val fullResponseStateEndInstances = listOf(
        StateEnd.True(aFullYesResponse),
        StateEnd.False(aFullNoResponse),
        StateEnd.Halt(aFullExceptionResponse)
    )

    private val stateEndCorrectInstances by lazy {
        minimalResponseStateEndInstances + fullResponseStateEndInstances
    }

    @Test
    fun testingPreconditions() {
        assertEquals(
            SideEffectManagerImpl(), SideEffectManagerImpl(),
            "Two empty SideEffectManager instances should be equal."
        )
    }

    @Test
    fun allStateEndInstancesBehaveDoesNothing() {
        stateEndCorrectInstances.forEach { assertEquals(emptySequence(), it.behave()) }
    }

    @Test
    fun filledResponseStateEndInstancesContextIsCreatedFromHeldSolveResponse() {
        fullResponseStateEndInstances.forEach {
            assertSame(it.solve.libraries, it.context.libraries)
            assertSame(it.solve.flags, it.context.flags)
            assertSame(it.solve.dynamicKb, it.context.dynamicKb)
            assertSame(it.solve.staticKb, it.context.staticKb)
        }
    }

    @Test
    fun minimalResponseStateEndInstancesContextIsCreatedWithDefaultValues() {
        minimalResponseStateEndInstances.forEach {
            assertEquals(Libraries(), it.context.libraries)
            assertEquals(mapOf(), it.context.flags)
            assertEquals(Theory.empty(), it.context.dynamicKb)
            assertEquals(Theory.empty(), it.context.staticKb)
        }
    }

    @Test
    fun trueContainsInsertedData() {
        listOf(aMinimalYesResponse, aFullYesResponse).forEach {
            assertSame(it, StateEnd.True(it).solve)
        }
    }

    @Test
    fun trueConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Yes }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.True(it) }
        }
    }

    @Test
    fun falseContainsInsertedData() {
        listOf(aMinimalNoResponse, aFullNoResponse).forEach {
            assertSame(it, StateEnd.False(it).solve)
        }
    }

    @Test
    fun falseConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.No }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.False(it) }
        }
    }

    @Test
    fun haltContainsInsertedData() {
        listOf(aMinimalExceptionResponse, aFullExceptionResponse).forEach {
            assertSame(it, StateEnd.Halt(it).solve)
            assertSame(
                (it.solution as Solution.Halt).exception,
                StateEnd.Halt(it).exception
            )
        }
    }

    @Test
    fun haltConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Halt }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.Halt(it) }
        }
    }

    @Test
    fun stateEndTrueForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndTrue(
            aSubstitution,
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager
        )

        with(toBeTested.solve) {
            assertEquals(aSubstitution, solution.substitution)
            assertEquals(theIntermediateStateRequest.query, solution.query)
        }
        assertStateContentsCorrect(
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun statEndTrueHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndTrue()

        assertEquals(Substitution.empty(), toBeTested.solve.solution.substitution)

        assertStateContentsCorrect(
            defaultLibraries,
            defaultFlags,
            defaultStaticKb,
            defaultDynamicKb,
            theRequestSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun stateEndFalseForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndFalse(
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager
        )

        assertEquals(theIntermediateStateRequest.query, toBeTested.solve.solution.query)

        assertStateContentsCorrect(
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun statEndFalseHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndFalse()

        assertStateContentsCorrect(
            defaultLibraries,
            defaultFlags,
            defaultStaticKb,
            defaultDynamicKb,
            theRequestSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun stateEndHaltForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndHalt(
            anException,
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager
        )

        with(toBeTested.solve) {
            assertEquals(anException, (solution as Solution.Halt).exception)
            assertEquals(theIntermediateStateRequest.query, solution.query)
        }
        assertStateContentsCorrect(
            someLibraries,
            someFlags,
            aStaticKb,
            aDynamicKb,
            aDifferentSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun statEndHaltHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndHalt(anException)

        assertStateContentsCorrect(
            defaultLibraries,
            defaultFlags,
            defaultStaticKb,
            defaultDynamicKb,
            theRequestSideEffectManager,
            toBeTested
        )
    }

    @Test
    fun stateEndWithSolutionForwardsToTheCorrectStateEndMethod() {
        allResponseTypes.map { it.solution }.forEach { responseSolution ->
            val toBeTested1 = anIntermediateState.stateEnd(responseSolution)

            assertStateContentsCorrect(
                defaultLibraries,
                defaultFlags,
                defaultStaticKb,
                defaultDynamicKb,
                theRequestSideEffectManager,
                toBeTested1
            )

            when (responseSolution) {
                is Solution.Yes -> {
                    assertEquals(StateEnd.True::class, toBeTested1::class)
                    assertEquals(responseSolution.substitution, toBeTested1.solve.solution.substitution)
                }
                is Solution.No -> assertEquals(StateEnd.False::class, toBeTested1::class)
                is Solution.Halt -> {
                    assertEquals(StateEnd.Halt::class, toBeTested1::class)
                    assertEquals(responseSolution.exception, (toBeTested1.solve.solution as Solution.Halt).exception)
                }
            }

            val toBeTested2 = anIntermediateState.stateEnd(
                responseSolution,
                someLibraries,
                someFlags,
                aStaticKb,
                aDynamicKb,
                aDifferentSideEffectManager
            )
            assertStateContentsCorrect(
                someLibraries,
                someFlags,
                aStaticKb,
                aDynamicKb,
                aDifferentSideEffectManager,
                toBeTested2
            )
        }
    }

    @Test
    fun stateEndWithResponseForwardsToCorrectStateEndMethodUsingResponseDataOrDefaultValuesIfNull() {
        allResponseTypes.forEach { response ->
            val toBeTested = anIntermediateState.stateEnd(response)

            assertEquals(response.solution, toBeTested.solve.solution)
            assertStateContentsCorrect(
                response.libraries ?: defaultLibraries,
                response.flags ?: defaultFlags,
                response.staticKb ?: defaultStaticKb,
                response.dynamicKb ?: defaultDynamicKb,
                theRequestSideEffectManager,
                toBeTested
            )
        }
    }

    @Test
    fun stateEndWithResponseSideEffectManagerTaking() {
        val contextImplInstance = StreamsExecutionContext(sideEffectManager = theRequestSideEffectManager)
        val differentContextImplInstance = StreamsExecutionContext(sideEffectManager = aDifferentSideEffectManager)
            .also { assertNotEquals(it, contextImplInstance) }

        val endStateForwardingExceptionalResponseWithNonNullSideEffectManager = anIntermediateState.stateEnd(
            Solve.Response(
                aQuery.halt(TuPrologRuntimeException(context = contextImplInstance)),
                sideEffectManager = aDifferentSideEffectManager
            )
        )
        assertEquals(
            aDifferentSideEffectManager,
            endStateForwardingExceptionalResponseWithNonNullSideEffectManager.solve.sideEffectManager,
            "stateEnd() should use provided Response SideEffectManager if not null"
        )

        val endStateForwardingExceptionalResponseWithNullSideEffectManager = anIntermediateState.stateEnd(
            Solve.Response(
                aQuery.halt(TuPrologRuntimeException(context = differentContextImplInstance))
            )
        )
        assertEquals(
            aDifferentSideEffectManager,
            endStateForwardingExceptionalResponseWithNullSideEffectManager.solve.sideEffectManager,
            "stateEnd() should use exception context's SideEffectManager if Response one is null"
        )

        val endStateForwardingResponseWithNullSideEffectManager = anIntermediateState.stateEnd(
            Solve.Response(aQuery.no())
        )
        assertEquals(
            theRequestSideEffectManager, endStateForwardingResponseWithNullSideEffectManager.solve.sideEffectManager,
            "stateEnd() should use current state solve.context to retrieve a SideEffectManager if no other available"
        )
    }
}
