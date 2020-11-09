package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

/**
 * Represents a Probabilistic Logic goal solver based on Prolog and backward compatible with it.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticSolver : Solver {

    @JsName("probWorldSolve")
    fun probWorldSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticWorldSolution>

    @JsName("probWorldSolveMaxDuration")
    fun probWorldSolve(goal: Struct): Sequence<ProbabilisticWorldSolution> =
            probWorldSolve(goal, TimeDuration.MAX_VALUE)

    @JsName("probSolve")
    fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticSolution>

    @JsName("probSolveMaxDuration")
    fun probSolve(goal: Struct): Sequence<ProbabilisticSolution> = probSolve(goal, TimeDuration.MAX_VALUE)

    companion object {
        // To be extended through extension methods
    }
}
