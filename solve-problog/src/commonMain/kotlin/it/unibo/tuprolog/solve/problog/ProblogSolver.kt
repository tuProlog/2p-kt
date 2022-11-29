package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.isProbabilistic
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbQuery
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.toProbConfigTerm
import it.unibo.tuprolog.solve.problog.lib.rules.Prob
import it.unibo.tuprolog.solve.setBinaryDecisionDiagram
import it.unibo.tuprolog.solve.setProbability
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal open class ProblogSolver(
    private val solver: Solver
) : Solver by solver {

    private fun solveNonProbabilistically(
        goal: Struct,
        options: SolveOptions
    ): Sequence<Solution> {
        val anonVar = Var.anonymous()
        return solver.solve(
            Tuple.of(
                Struct.of(
                    ProbSetConfig.functor,
                    options.toProbConfigTerm()
                ),
                Struct.of(Prob.functor, anonVar, goal),
            ),
            options
        ).map {
            when (it) {
                is Solution.Yes -> Solution.yes(
                    goal,
                    it.substitution.filter { key, _ -> key != anonVar }
                )
                is Solution.Halt -> Solution.halt(goal, it.exception)
                else -> Solution.no(goal)
            }
        }
    }

    private fun solveProbabilistically(
        goal: Struct,
        options: SolveOptions
    ): Sequence<Solution> {
        val probabilityVar = Var.of("Prob")
        val bddVar = Var.of("BDD")
        return solver.solve(
            Struct.of(
                ProbQuery.functor,
                probabilityVar,
                goal,
                options.toProbConfigTerm(),
                bddVar,
            ),
            options
        ).map {
            val probabilityTerm = it.substitution[probabilityVar]
            val bddTerm = it.substitution[bddVar]
            var newSolution = when (it) {
                is Solution.Yes -> Solution.yes(
                    goal,
                    it.substitution.filter {
                        key, _ ->
                        key != probabilityVar && key != bddVar
                    }
                )
                is Solution.Halt -> Solution.halt(goal, it.exception)
                else -> Solution.no(goal)
            }
            if (!options.isProbabilistic) {
                newSolution
            } else {
                // Set the probability property
                newSolution = when (probabilityTerm) {
                    is Numeric -> newSolution.setProbability(
                        probabilityTerm.decimalValue.toDouble()
                    )
                    else -> newSolution.setProbability(Double.NaN)
                }

                // Set the Binary Decision Diagram property
                if (bddTerm != null && bddTerm is ProbExplanationTerm) {
                    val explanation = bddTerm.explanation
                    if (explanation is BinaryDecisionDiagramExplanation) {
                        newSolution = newSolution.setBinaryDecisionDiagram(
                            explanation.diagram
                        )
                    }
                }
                newSolution
            }
        }
    }

    override fun solve(
        goal: Struct,
        options: SolveOptions
    ): Sequence<Solution> =
        if (!options.isProbabilistic) {
            solveNonProbabilistically(goal, options)
        } else {
            solveProbabilistically(goal, options)
        }

    override fun solve(
        goal: Struct,
        timeout: TimeDuration
    ): Sequence<Solution> =
        solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    override fun solve(
        goal: Struct
    ): Sequence<Solution> = solve(goal, SolveOptions.DEFAULT)

    override fun solveList(
        goal: Struct,
        timeout: TimeDuration
    ): List<Solution> = solve(goal, timeout).toList()

    override fun solveList(
        goal: Struct
    ): List<Solution> = solve(goal).toList()

    override fun solveList(
        goal: Struct,
        options: SolveOptions
    ): List<Solution> = solve(goal, options).toList()

    override fun solveOnce(
        goal: Struct,
        timeout: TimeDuration
    ): Solution =
        solve(goal, SolveOptions.someLazilyWithTimeout(1, timeout)).first()

    override fun solveOnce(
        goal: Struct
    ): Solution = solve(goal, SolveOptions.someLazily(1)).first()

    override fun solveOnce(
        goal: Struct,
        options: SolveOptions
    ): Solution = solve(goal, options.setLimit(1)).first()

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): Solver {
        return ProblogSolver(
            solver.copy(
                unificator,
                libraries,
                flags,
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings
            )
        )
    }

    override fun clone(): Solver {
        return ProblogSolver(solver.clone())
    }
}
