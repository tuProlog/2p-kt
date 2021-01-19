package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.InputStore
import it.unibo.tuprolog.solve.OutputStore
import it.unibo.tuprolog.solve.ProbSolution
import it.unibo.tuprolog.solve.ProbSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbQuery
import it.unibo.tuprolog.theory.Theory

internal open class ProblogProbSolver(
    private val solver: Solver
) : ProbSolver {

    private fun innerSolve(probabilityVar: Var, goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        return solver.solve(Struct.of(ProbQuery.functor, probabilityVar, goal), maxDuration).map {
            when (it) {
                is Solution.Yes -> Solution.Yes(it.solvedQuery[1] as Struct, it.substitution)
                else -> Solution.No(it.query)
            }
        }
    }

    override fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbSolution> {
        val probabilityVar = Var.of("Probability")
        return innerSolve(probabilityVar, goal, maxDuration).map {
            when (val probTerm = it.substitution[probabilityVar]) {
                is Numeric -> ProbSolution(it, probTerm.decimalValue.toDouble())
                else -> ProbSolution(it, Double.NaN)
            }
        }
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        return innerSolve(Var.of("Probability"), goal, TimeDuration.MAX_VALUE)
    }

    override val libraries: Libraries
        get() = solver.libraries

    override val flags: FlagStore
        get() = solver.flags

    override val staticKb: Theory
        get() = solver.staticKb

    override val dynamicKb: Theory
        get() = solver.dynamicKb

    override val operators: OperatorSet
        get() = solver.operators

    override val inputChannels: InputStore<*>
        get() = solver.inputChannels

    override val outputChannels: OutputStore<*>
        get() = solver.outputChannels
}
