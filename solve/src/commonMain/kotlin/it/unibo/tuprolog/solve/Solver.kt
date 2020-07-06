package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver : ExecutionContextAware {

    /** Solves the provided goal, returning lazily initialized sequence of solutions, optionally limiting computation [maxDuration] */
    @JsName("solve")
    fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution>

    @JsName("solveMaxDuration")
    fun solve(goal: Struct): Sequence<Solution> = solve(goal, TimeDuration.MAX_VALUE)

    companion object {
        // To be extended through extension methods
    }
}
