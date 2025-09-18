package it.unibo.tuprolog.solve.streams.solver.fsm

/**
 * A wrapper class representing States that should not be executed again, because they've already executed their behaviour
 *
 * @author Enrico
 */
internal class AlreadyExecutedState(
    internal val wrappedState: State,
) : State by wrappedState {
    override val hasBehaved: Boolean = true

    override fun toString(): String = "AlreadyExecutedState of: $wrappedState"
}

/** Extension method to wrap a [State], marking it as already executed */
internal fun State.asAlreadyExecuted(): AlreadyExecutedState =
    (this as? AlreadyExecutedState)
        ?.let { it }
        ?: AlreadyExecutedState(this)
