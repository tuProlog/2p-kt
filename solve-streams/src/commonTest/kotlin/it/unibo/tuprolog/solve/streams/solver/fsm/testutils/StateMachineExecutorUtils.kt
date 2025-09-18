package it.unibo.tuprolog.solve.streams.solver.fsm.testutils

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor
import kotlin.reflect.KClass
import kotlin.test.fail

/**
 * Utils singleton for testing [StateMachineExecutor]
 *
 * @author Enrico
 */
internal object StateMachineExecutorUtils {
    /** A dummy state that has not already behaved and whose behaviour returns an emptySequence, making execution stop */
    private val defaultDummyEndState =
        object : State {
            override val solve: Solve by lazy<Solve> { throw NotImplementedError() }

            override fun behave(): Sequence<State> = emptySequence()

            override val hasBehaved: Boolean = false
            override val context: StreamsExecutionContext by lazy<StreamsExecutionContext> {
                throw NotImplementedError()
            }

            override fun toString(): String = this::class.className()
        }

    /** A state behaving as an end state, it returns an emptySequence behaving, and this should make computation stop */
    internal val endState = defaultDummyEndState

    /**
     * Creates a state that returns other copies of itself for [nextCountToProceed] times before going into [otherState]
     *
     * `state->state->state->...->otherState`
     */
    internal fun oneStateAtATimeState(
        nextCountToProceed: Int,
        otherState: State,
    ): State =
        object : State by defaultDummyEndState {
            override fun behave(): Sequence<State> =
                sequence {
                    if (nextCountToProceed > 0) {
                        yield(oneStateAtATimeState(nextCountToProceed - 1, otherState))
                    } else {
                        yield(otherState)
                    }
                }

            override fun toString(): String = "${this::class.className()} nextCountToProceed:$nextCountToProceed"
        }

    /**
     * Creates a state that returns [nextCount] alternative paths to [nextState]
     * ```
     *        >   nextState
     *      / ...
     * this - - > nextState
     *      \ ...
     *        >   nextState
     * ```
     */
    internal fun allNextStatesFromThis(
        nextCount: Int,
        nextState: State,
    ): State =
        object : State by defaultDummyEndState {
            override fun behave(): Sequence<State> =
                sequence {
                    repeat(nextCount) { yield(nextState) }
                }

            override fun toString(): String = "${this::class.className()} nextCount:$nextCount"
        }

    /** Returns the same state but with [State.hasBehaved] set true, and before behave execution it launches [onBehaveAction] hook */
    internal fun toBehavedState(
        state: State,
        onBehaveAction: () -> Unit = { fail("Behave method should not be called for `hasBehaved = true` states") },
    ) = object : State by state {
        override val hasBehaved: Boolean = true

        override fun behave(): Sequence<State> = onBehaveAction().run { state.behave() }

        override fun toString(): String = "${this::class.className()}  originalState: $state"
    }

    /** A state that goes directly to [endState] */
    internal val oneShotState = oneStateAtATimeState(0, endState)

    /** A state that goes into [endState] after three calls of [State.behave] */
    internal val threeShotState = oneStateAtATimeState(3, endState)

    /** A state that generates two alternative paths both going into [endState] */
    internal val twoAlternativeState = allNextStatesFromThis(2, endState)

    /** A state that generates a search Tree with eight leafs */
    internal val eightLeafSearchTreeState =
        allNextStatesFromThis(
            2,
            allNextStatesFromThis(
                2,
                allNextStatesFromThis(2, endState),
            ),
        )

    /** A [threeShotState] that won't produce any state because already executed */
    internal val threeShotStateAlreadyExecuted = toBehavedState(threeShotState)

    /** A `fiveShotState` that has the third state that has already executed */
    internal val thirdStateHasAlreadyBehaved =
        oneStateAtATimeState(
            2,
            toBehavedState(oneStateAtATimeState(2, endState)),
        )

    /** All state machines that are finite */
    internal val allFiniteStateMachines =
        listOf(
            oneShotState,
            threeShotState,
            twoAlternativeState,
            eightLeafSearchTreeState,
            threeShotStateAlreadyExecuted,
            thirdStateHasAlreadyBehaved,
        )

    /** Utility function to print meaningful names in object toString */
    private fun KClass<*>.className() =
        this
            .toString()
            .substringAfter("$")
            .substringBefore("(")
            .trim()
}
