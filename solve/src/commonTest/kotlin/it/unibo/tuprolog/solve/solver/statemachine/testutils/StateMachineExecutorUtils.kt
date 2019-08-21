package it.unibo.tuprolog.solve.solver.statemachine.testutils

import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.state.State
import kotlin.reflect.KClass

/**
 * Utils singleton for testing [StateMachineExecutor]
 *
 * @author Enrico
 */
internal object StateMachineExecutorUtils {

    /** A dummy state whose behaviour returns an emptySequence, making execution stop */
    private val defaultDummyEndState = object : State {
        override val solveRequest: Nothing by lazy { throw NotImplementedError() }
        override fun behave(): Sequence<State> = emptySequence()
        override fun toString(): String = this::class.className()
    }

    /** A state behaving as an end state, it returns an emptySequence behaving, and this should make computation stop */
    internal val endState = defaultDummyEndState

    /**
     * Creates a state that returns other copies of itself for [nextCountToProceed] times before going into [otherState]
     *
     * `state->state->state->...->otherState`
     */
    internal fun oneStateAtATimeState(nextCountToProceed: Int, otherState: State): State = object : State by defaultDummyEndState {
        override fun behave(): Sequence<State> = sequence {
            if (nextCountToProceed > 0) yield(oneStateAtATimeState(nextCountToProceed - 1, otherState))
            else yield(otherState)
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
    internal fun allNextStatesFromThis(nextCount: Int, nextState: State): State = object : State by defaultDummyEndState {
        override fun behave(): Sequence<State> = sequence {
            repeat(nextCount) { yield(nextState) }
        }

        override fun toString(): String = "${this::class.className()} nextCount:$nextCount"
    }

    /** A state that goes directly to [endState] */
    internal val oneShotState = oneStateAtATimeState(0, endState)
    /** A state that goes into [endState] after three calls of [State.behave] */
    internal val threeShotState = oneStateAtATimeState(3, endState)
    /** A state that generates two alternative paths both going into [endState] */
    internal val twoAlternativeState = allNextStatesFromThis(2, endState)
    /** A state that generates a search Tree with eight leafs */
    internal val eightLeafSearchTreeState =
            allNextStatesFromThis(2,
                    allNextStatesFromThis(2,
                            allNextStatesFromThis(2, endState)))

    /** All state machines that are finite */
    internal val allFiniteStateMachines = listOf(
            oneShotState,
            threeShotState,
            twoAlternativeState,
            eightLeafSearchTreeState
    )

    /** Utility function to print meaningful names in object toString */
    private fun KClass<*>.className() = this.toString().substringAfter("$").substringBefore("(").trim()
}
