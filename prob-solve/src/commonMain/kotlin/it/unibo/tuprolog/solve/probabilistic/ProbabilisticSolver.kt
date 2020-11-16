package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import kotlin.js.JsName

/**
 * Represents a probabilistic logic goal solver, based on Prolog and backwards compatible with it.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticSolver : Solver {
    @JsName("probSolveWorld")
    fun probSolveWorld(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticWorld>

    @JsName("probSolveWorldMaxDuration")
    fun probSolveWorld(goal: Struct): Sequence<ProbabilisticWorld> = probSolveWorld(goal, TimeDuration.MAX_VALUE)

    @JsName("probSolve")
    fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticSolution>

    @JsName("probSolveMaxDuration")
    fun probSolve(goal: Struct): Sequence<ProbabilisticSolution> = probSolve(goal, TimeDuration.MAX_VALUE)

    companion object {
        // To be extended through extension methods
    }
}
