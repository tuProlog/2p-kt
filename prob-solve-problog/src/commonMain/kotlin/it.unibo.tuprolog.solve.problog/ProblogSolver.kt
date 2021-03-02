package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.isProbabilistic
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbQuery
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetMode
import it.unibo.tuprolog.solve.setProbability
import it.unibo.tuprolog.theory.Theory

internal open class ProblogSolver(
    private val solver: Solver
) : Solver by solver {

    override fun solve(goal: Struct, options: SolveOptions): Sequence<Solution> {
        val probabilityVar = Var.of("Prob")
        val mode = if (options.isProbabilistic) ProbSetMode.ProblogMode else ProbSetMode.PrologMode
        return solver.solve(Struct.of(ProbQuery.functor, probabilityVar, goal, mode), options)
            .map {
                val probabilityTerm = it.substitution[probabilityVar]
                val newSolution = when (it) {
                    is Solution.Yes -> Solution.yes(
                        it.solvedQuery[1] as Struct,
                        it.substitution.filter { key, _ -> key != probabilityVar }
                    )
                    else -> Solution.no(it.query)
                }
                if (!options.isProbabilistic) {
                    newSolution
                } else when (probabilityTerm) {
                    is Numeric -> newSolution.setProbability(probabilityTerm.decimalValue.toDouble())
                    else -> newSolution.setProbability(Double.NaN)
                }
            }
    }

    override fun solve(goal: Struct, timeout: TimeDuration): Sequence<Solution> =
        solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    override fun solve(goal: Struct): Sequence<Solution> = solve(goal, SolveOptions.DEFAULT)

    override fun solveList(goal: Struct, timeout: TimeDuration): List<Solution> = solve(goal, timeout).toList()

    override fun solveList(goal: Struct): List<Solution> = solve(goal).toList()

    override fun solveList(goal: Struct, options: SolveOptions): List<Solution> = solve(goal, options).toList()

    override fun solveOnce(goal: Struct, timeout: TimeDuration): Solution =
        solve(goal, SolveOptions.someLazilyWithTimeout(1, timeout)).first()

    override fun solveOnce(goal: Struct): Solution = solve(goal, SolveOptions.someLazily(1)).first()

    override fun solveOnce(goal: Struct, options: SolveOptions): Solution = solve(goal, options.setLimit(1)).first()

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver {
        return ProblogSolver(solver.copy(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings))
    }

    override fun clone(): Solver {
        return ProblogSolver(solver.clone())
    }
}
