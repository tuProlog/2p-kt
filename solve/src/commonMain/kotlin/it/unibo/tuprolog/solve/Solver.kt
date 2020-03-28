package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver : ExecutionContextAware {

    /** Solves the provided goal, returning lazily initialized sequence of solutions, optionally limiting computation [maxDuration] */
    fun solve(goal: Struct, maxDuration: TimeDuration = TimeDuration.MAX_VALUE): Sequence<Solution>

    companion object {
        // To be extended through extension methods
    }
}
