package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

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

        @JvmStatic
        @JsName("classic")
        val classic: SolverFactory by lazy {
            classicSolverFactory()
        }

        @JvmStatic
        @JsName("streams")
        val streams: SolverFactory by lazy {
            streamsSolverFactory()
        }
    }
}
