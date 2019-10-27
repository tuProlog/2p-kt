package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of solutions, optionally limiting computation [maxDuration] */
    fun solve(goal: Struct, maxDuration: TimeDuration = TimeDuration.MAX_VALUE): Sequence<Solution>

    /** Loaded libraries */
    val libraries: Libraries

    /** Enabled flags */
    val flags: Map<Atom, Term>

    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
    val staticKB: ClauseDatabase

    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
    val dynamicKB: ClauseDatabase

    companion object {
        // To be extended through extension methods
    }
}

inline fun Solver.solve(maxDuration: TimeDuration = TimeDuration.MAX_VALUE, scopedContext: Scope.() -> Struct): Sequence<Solution> =
        solve(scopedContext(Scope.empty()), maxDuration)
