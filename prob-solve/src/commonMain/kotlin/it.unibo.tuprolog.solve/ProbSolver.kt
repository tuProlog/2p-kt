package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

/**
 * Prolog Goal solver for probabilistic logic queries which also backwards compatible with simple Prolog
 *
 * @author Jason Dellaluce
 */
interface ProbSolver : Solver {

    companion object {
        // To be extended through extension methods
    }

    @JsName("probSolve")
    fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbSolution>

    @JsName("probSolveMaxDuration")
    fun probSolve(goal: Struct): Sequence<ProbSolution> = probSolve(goal, TimeDuration.MAX_VALUE)
}
