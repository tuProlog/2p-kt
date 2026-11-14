package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionStream
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
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
import it.unibo.tuprolog.solve.toGoal
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.time.Duration

internal open class ProblogSolver(
    private val solver: Solver,
) : Solver by solver {
    private fun solveNonProbabilistically(
        goal: Goal,
        options: SolveOptions,
    ): SolutionStream {
        val anonVar = Var.anonymous()
        return solver
            .solve(
                Tuple.of(
                    Struct.of(
                        ProbSetConfig.functor,
                        options.toProbConfigTerm(),
                    ),
                    Struct.of(Prob.functor, anonVar, goal.body),
                ),
                options,
            ).mapSolutions {
                when (it) {
                    is Solution.Yes ->
                        Solution.yes(
                            goal,
                            it.substitution.filter { key, _ -> key != anonVar },
                        )
                    is Solution.Halt -> Solution.halt(goal, it.exception)
                    else -> Solution.no(goal)
                }
            }
    }

    private fun solveProbabilistically(
        goal: Goal,
        options: SolveOptions,
    ): SolutionStream {
        val probabilityVar = Var.of("Prob")
        val bddVar = Var.of("BDD")
        return solver
            .solve(
                Struct.of(
                    ProbQuery.functor,
                    probabilityVar,
                    goal.body,
                    options.toProbConfigTerm(),
                    bddVar,
                ),
                options,
            ).mapSolutions {
                val probabilityTerm = it.substitution[probabilityVar]
                val bddTerm = it.substitution[bddVar]
                var newSolution =
                    when (it) {
                        is Solution.Yes ->
                            Solution.yes(
                                goal,
                                it.substitution.filter { key, _ ->
                                    key != probabilityVar && key != bddVar
                                },
                            )
                        is Solution.Halt -> Solution.halt(goal, it.exception)
                        else -> Solution.no(goal)
                    }
                if (!options.isProbabilistic) {
                    newSolution
                } else {
                    // Set the probability property
                    newSolution =
                        when (probabilityTerm) {
                            is Numeric ->
                                newSolution.setProbability(
                                    probabilityTerm.decimalValue.toDouble(),
                                )
                            else -> newSolution.setProbability(Double.NaN)
                        }

                    // Set the Binary Decision Diagram property
                    if (bddTerm != null && bddTerm is ProbExplanationTerm) {
                        val explanation = bddTerm.explanation
                        if (explanation is BinaryDecisionDiagramExplanation) {
                            newSolution =
                                newSolution.setBinaryDecisionDiagram(
                                    explanation.diagram,
                                )
                        }
                    }
                    newSolution
                }
            }
    }

    override fun solve(
        goal: Goal,
        options: SolveOptions,
    ): SolutionStream =
        if (!options.isProbabilistic) {
            solveNonProbabilistically(goal, options)
        } else {
            solveProbabilistically(goal, options)
        }

    override fun solve(
        goal: Compound,
        options: SolveOptions,
    ): SolutionStream = solve(goal.toGoal(), options)

    override fun solve(
        goal: Compound,
        timeout: Duration,
    ): SolutionStream = solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    override fun solve(goal: Compound): SolutionStream = solve(goal, SolveOptions.DEFAULT)

    // overridden so that the delegation to solver does not bypass the probabilistic solve above
    @Deprecated(
        "Call solve(goal, timeout).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal, timeout).toList()"),
    )
    override fun solveList(
        goal: Compound,
        timeout: Duration,
    ): List<Solution> = solve(goal, timeout).toList()

    @Deprecated(
        "Call solve(goal).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal).toList()"),
    )
    override fun solveList(goal: Compound): List<Solution> = solve(goal).toList()

    @Deprecated(
        "Call solve(goal, options).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal, options).toList()"),
    )
    override fun solveList(
        goal: Compound,
        options: SolveOptions,
    ): List<Solution> = solve(goal, options).toList()

    override fun solveOnce(
        goal: Compound,
        timeout: Duration,
    ): Solution = solveOnce(goal, SolveOptions.someLazilyWithTimeout(1, timeout))

    override fun solveOnce(goal: Compound): Solution = solveOnce(goal, SolveOptions.someLazily(1))

    override fun solveOnce(
        goal: Compound,
        options: SolveOptions,
    ): Solution = solve(goal, options.setLimit(1)).use { it.first() }

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): Solver =
        ProblogSolver(
            solver.copy(
                unificator,
                libraries,
                flags,
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings,
            ),
        )

    override fun clone(): Solver = ProblogSolver(solver.clone())
}
