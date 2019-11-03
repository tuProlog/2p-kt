package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aDifferentSideEffectManager
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aDynamicKB
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aNoResponse
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aStaticKB
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aSubstitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aYesResponse
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.allResponseTypes
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.anException
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.anExceptionalResponse
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.anIntermediateState
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.assertStateContentsCorrect
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.someFlags
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.someLibraries
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.theIntermediateStateRequest
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.theRequestSideEffectManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal class StateEndTest {

    private val stateEndCorrectInstances by lazy {
        listOf(
                StateEnd.True(aYesResponse),
                StateEnd.False(aNoResponse),
                StateEnd.Halt(anExceptionalResponse)
        )
    }

    @Test
    fun allStateEndInstancesBehaveDoesNothing() {
        stateEndCorrectInstances.forEach { assertEquals(emptySequence(), it.behave()) }
    }

    @Test
    fun trueContainsInsertedData() {
        assertSame(aYesResponse, StateEnd.True(aYesResponse).solve)
    }

    @Test
    fun trueConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Yes }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.True(it) }
        }
    }

    @Test
    fun falseContainsInsertedData() {
        assertSame(aNoResponse, StateEnd.False(aNoResponse).solve)
    }

    @Test
    fun falseConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.No }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.False(it) }
        }
    }

    @Test
    fun haltContainsInsertedData() {
        assertSame(anExceptionalResponse, StateEnd.Halt(anExceptionalResponse).solve)
        assertSame((anExceptionalResponse.solution as Solution.Halt).exception, StateEnd.Halt(anExceptionalResponse).exception)
    }

    @Test
    fun haltConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Halt }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.Halt(it) }
        }
    }

    @Test
    fun stateEndTrueForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndTrue(aSubstitution, someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager)

        with(toBeTested.solve) {
            assertEquals(aSubstitution, solution.substitution)
            assertEquals(theIntermediateStateRequest.query, solution.query)
        }
        assertStateContentsCorrect(someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager, toBeTested)
    }

    @Test
    fun statEndTrueHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndTrue()

        assertEquals(Substitution.empty(), toBeTested.solve.solution.substitution)

        assertStateContentsCorrect(null, null, null, null, theRequestSideEffectManager, toBeTested)
    }

    @Test
    fun stateEndFalseForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndFalse(someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager)

        assertEquals(theIntermediateStateRequest.query, toBeTested.solve.solution.query)

        assertStateContentsCorrect(someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager, toBeTested)
    }

    @Test
    fun statEndFalseHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndFalse()

        assertStateContentsCorrect(null, null, null, null, theRequestSideEffectManager, toBeTested)
    }

    @Test
    fun stateEndHaltForwardsCorrectlyParameters() {
        val toBeTested = anIntermediateState.stateEndHalt(anException, someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager)

        with(toBeTested.solve) {
            assertEquals(anException, (solution as Solution.Halt).exception)
            assertEquals(theIntermediateStateRequest.query, solution.query)
        }
        assertStateContentsCorrect(someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager, toBeTested)
    }

    @Test
    fun statEndHaltHasCorrectDefaultParameters() {
        val toBeTested = anIntermediateState.stateEndHalt(anException)

        assertStateContentsCorrect(null, null, null, null, theRequestSideEffectManager, toBeTested)
    }

    @Test
    fun stateEndWithSolutionForwardsToTheCorrectStateEndMethod() {
        allResponseTypes.map { it.solution }.forEach { responseSolution ->

            anIntermediateState.stateEnd(responseSolution).also { stateEndUnderTest ->
                assertStateContentsCorrect(null, null, null, null, theRequestSideEffectManager, stateEndUnderTest)

                when (responseSolution) {
                    is Solution.Yes -> {
                        assertEquals(StateEnd.True::class, stateEndUnderTest::class)
                        assertEquals(responseSolution.substitution, stateEndUnderTest.solve.solution.substitution)
                    }
                    is Solution.No -> assertEquals(StateEnd.False::class, stateEndUnderTest::class)
                    is Solution.Halt -> {
                        assertEquals(StateEnd.Halt::class, stateEndUnderTest::class)
                        assertEquals(responseSolution.exception, (stateEndUnderTest.solve.solution as Solution.Halt).exception)
                    }
                }
            }

            val toBeTested2 = anIntermediateState.stateEnd(responseSolution, someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager)
            assertStateContentsCorrect(someLibraries, someFlags, aStaticKB, aDynamicKB, aDifferentSideEffectManager, toBeTested2)
        }
    }

    @Test
    fun stateEndWithResponseForwardsToCorrectStateEndMethodUsingResponseData() {
        allResponseTypes.forEach { response ->
            val toBeTested = anIntermediateState.stateEnd(response)

            assertEquals(response.solution, toBeTested.solve.solution)
            assertStateContentsCorrect(response.libraries, response.flags, response.staticKB, response.dynamicKB, response.sideEffectManager, toBeTested)
        }
    }
}
