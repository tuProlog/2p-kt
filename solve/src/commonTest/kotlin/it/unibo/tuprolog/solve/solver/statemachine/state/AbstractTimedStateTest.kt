package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractTimedState]
 *
 * @author Enrico
 */
internal class AbstractTimedStateTest {

    private val behaviourResponse = emptySequence<Nothing>()

    /** Creates an [AbstractTimedState] instance with provided parameters, and emptySequence returning behaviour */
    private fun createAbstractTimeState(solveRequest: Solve.Request, executionStrategy: CoroutineScope) =
            object : AbstractTimedState(solveRequest, executionStrategy) {
                override fun behaveTimed(): Sequence<State> = behaviourResponse
            }

    @Test
    fun behaveWorksAsUsualIfLongMaxValueTimeoutSpecified() {
        val toBeTested = createAbstractTimeState(
                DummyInstances.solveRequest.copy(executionTimeout = Long.MAX_VALUE),
                DummyInstances.executionStrategy
        )

        assertEquals(behaviourResponse, toBeTested.behave())
    }

    @Test
    fun behaveChecksIfTimeoutElapsedAndIfItIsYieldsStateEndHalt() {
        val toBeTested = createAbstractTimeState(
                DummyInstances.solveRequest.copy(executionTimeout = 0),
                DummyInstances.executionStrategy
        )

        val state = toBeTested.behave().single()
        assertEquals(state::class, StateEnd.Halt::class)
        assertEquals((state as StateEnd.Halt).exception::class, TimeOutException::class)
    }

    @Test
    fun behaveChecksIfTimeoutElapsedAndIfNotGoesIntoBehaveTimedBehaviour() {
        val toBeTested = createAbstractTimeState(
                with(DummyInstances.solveRequest) {
                    copy(
                            context = context.copy(computationStartTime = currentTime()),
                            executionTimeout = 1000
                    )
                },
                DummyInstances.executionStrategy
        )

        assertEquals(behaviourResponse, toBeTested.behave())
    }

    @Test
    fun behaveCanBeCalledMultipleTimesYieldingAlwaysSameResponse() {
        val toBeTested = createAbstractTimeState(
                DummyInstances.solveRequest.copy(
                        context = DummyInstances.executionContext.copy(computationStartTime = currentTime()),
                        executionTimeout = 20
                ),
                DummyInstances.executionStrategy
        )

        repeat(1000) { assertEquals(behaviourResponse.toList(), toBeTested.behave().toList()) }
    }

    @Test
    fun getCurrentTimeReturnsCurrentTime() {
        val correct = currentTime()
        val toBeTested = createAbstractTimeState(DummyInstances.solveRequest, DummyInstances.executionStrategy).getCurrentTime()
        assertEquals(correct, toBeTested)
    }
}
