package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ProbSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbQuery
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetMode
import it.unibo.tuprolog.solve.setProbability

internal open class ProblogProbSolver(
    private val solver: Solver
) : ProbSolver, Solver by solver {

    private fun innerSolve(probabilityVar: Var, goal: Struct, mode: Atom, maxDuration: TimeDuration): Sequence<Solution> {
        return solver.solve(Struct.of(ProbQuery.functor, probabilityVar, goal, mode), maxDuration).map {
            when (it) {
                is Solution.Yes -> Solution.yes(it.solvedQuery[1] as Struct, it.substitution)
                else -> Solution.no(it.query)
            }
        }
    }

    override fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        val probabilityVar = Var.of("Probability")
        return innerSolve(probabilityVar, goal, ProbSetMode.ProblogMode, maxDuration).map {
            when (val probTerm = it.substitution[probabilityVar]) {
                is Numeric -> it.setProbability(probTerm.decimalValue.toDouble())
                else -> it.setProbability(Double.NaN)
            }
        }
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        return innerSolve(Var.of("Probability"), goal, ProbSetMode.PrologMode, TimeDuration.MAX_VALUE)
    }
}
