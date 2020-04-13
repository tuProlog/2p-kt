package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver : ExecutionContextAware {

    /** Solves the provided goal, returning lazily initialized sequence of solutions, optionally limiting computation [maxDuration] */
    @JsName("solve")
    fun solve(goal: Struct, maxDuration: TimeDuration = TimeDuration.MAX_VALUE): Sequence<Solution>

    companion object {
        // To be extended through extension methods
    }
}
