package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

/**
 * A terminal [State] for one branch of the search tree: it carries the [Solution] that branch produced ([StateEnd]
 * for a successful/failed one, [StateHalt] for one aborted by an uncaught exception) and has no successors.
 *
 * Reaching an [EndState] is what triggers publishing [solution] onto the resolution's shared solution channel (see
 * `it.unibo.tuprolog.solve.concurrent.ConcurrentResolutionHandle.publishSolutionAndTerminateResolutionIfNeed`);
 * every other branch of the tree keeps running independently until it, too, reaches its own [EndState].
 */
interface EndState : State {
    override val isEndState: Boolean
        get() = true

    /** The [Solution] produced by this branch of the search tree. */
    @JsName("solution")
    val solution: Solution

    override fun asEndState(): EndState = this

    override fun next(): Iterable<State> = emptyList()
}
