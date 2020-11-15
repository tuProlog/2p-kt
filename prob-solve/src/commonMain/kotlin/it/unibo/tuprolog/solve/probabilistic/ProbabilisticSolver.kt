package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import kotlin.js.JsName

/**
 * Represents a Probabilistic Logic goal solver, based on Prolog and backward compatible with it.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticSolver : Solver {

    @JsName("probProve")
    fun probProve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticProof>

    @JsName("probProveMaxDuration")
    fun probProve(goal: Struct): Sequence<ProbabilisticProof> =
            probProve(goal, TimeDuration.MAX_VALUE)

    @JsName("probSolve")
    fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticSolution>

    @JsName("probSolveMaxDuration")
    fun probSolve(goal: Struct): Sequence<ProbabilisticSolution> = probSolve(goal, TimeDuration.MAX_VALUE)

    companion object {
        // To be extended through extension methods
    }
}
