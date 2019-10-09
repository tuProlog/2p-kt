package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of solutions, optionally limiting computation [maxDuration] */
    fun solve(goal: Struct, maxDuration: TimeDuration = TimeDuration.MAX_VALUE): Sequence<Solution>

    fun solve(maxDuration: TimeDuration = TimeDuration.MAX_VALUE, scopedContext: Scope.() -> Struct): Sequence<Solution> =
            solve(scopedContext(Scope.empty()), maxDuration)

    companion object {
        // To be extended through extension methods
    }
}
