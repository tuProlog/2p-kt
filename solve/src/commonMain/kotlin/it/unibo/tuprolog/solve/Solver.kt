package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

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
        val classic: SolverFactory by lazy {
            classicSolverFactory()
        }

    }
}
