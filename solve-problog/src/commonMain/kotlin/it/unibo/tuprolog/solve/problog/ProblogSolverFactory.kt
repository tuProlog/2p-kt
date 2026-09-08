package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsExport

/**
 * The [SolverFactory] for `:solve-problog`, a ProbLog-style probabilistic logic programming engine built on
 * top of [ClassicSolverFactory]'s SLD-NF resolution.
 *
 * ProbLog extends plain Prolog with *annotated disjunctions* -- clauses and facts whose head is prefixed by a
 * probability, using the `::` operator declared in [ANNOTATION_OPERATOR] (e.g. `0.3::burglary.`, read as
 * "burglary holds with probability 0.3"), and with `evidence/1`/`evidence/2` clauses stating facts known for
 * certain. Every [Solver] produced by this factory rewrites such a theory into a plain, Prolog-compliant one
 * (see the internal `it.unibo.tuprolog.solve.problog.lib.knowledge.impl` clause mappers) in which each
 * probabilistic clause carries along a Boolean "explanation" term; when a query is solved with
 * [it.unibo.tuprolog.solve.isProbabilistic] turned on (see [it.unibo.tuprolog.solve.SolveOptions]), those
 * per-solution explanations are compiled into a `:bdd` `BinaryDecisionDiagram` and weighted-model-counted to
 * compute the solution's overall probability -- exposed on each [it.unibo.tuprolog.solve.Solution] via
 * [it.unibo.tuprolog.solve.probability] and, when requested, the diagram itself via
 * [it.unibo.tuprolog.solve.binaryDecisionDiagram]. With probabilistic mode turned off, resolution behaves like
 * plain Prolog and annotations are ignored (every solution is treated as certain, i.e. probability 1.0).
 *
 * Solvers built by this factory always load [ProblogLib] (adding it to `libraries` if not already present) and
 * force the [TrackVariables] flag `ON`, because variable tracking is required to compute explanations correctly.
 *
 * @see it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
 * @see it.unibo.tuprolog.solve.isProbabilistic
 */
@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
object ProblogSolverFactory : SolverFactory {
    /** The standard library of this factory: [ProblogLib]'s builtins, contributing the `::` operator, the
     * probabilistic resolution primitives and rules, and (transitively) the classic Prolog builtins. */
    override val defaultBuiltins: Library
        get() = ProblogLib.DefaultBuiltins

    /** Same as [SolverFactory.defaultFlags], but with [TrackVariables] forced `ON`, since ProbLog resolution
     * relies on variable substitutions being tracked to build correct explanations. */
    override val defaultFlags: FlagStore
        get() = ensureVariablesTracking(super.defaultFlags)

    private fun ensureVariablesTracking(flags: FlagStore): FlagStore = flags + TrackVariables { ON }

    /** Same as [SolverFactory.solverOf], but ensures [ProblogLib] is loaded (see [fixLibraries]), forces
     * [TrackVariables] `ON`, and rewrites [staticKb]/[dynamicKb] into a
     * [it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory] before handing everything to
     * [ClassicSolverFactory]. */
    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): Solver =
        ProblogSolver(
            ClassicSolverFactory.solverOf(
                unificator,
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                inputs,
                outputs,
            ),
        )

    /** Adds [ProblogLib] to `libraries` if it isn't already loaded under its alias, so that the `::` operator
     * and the probabilistic resolution primitives/rules are always available regardless of what the caller
     * passed in. */
    private fun fixLibraries(libraries: Runtime): Runtime =
        if (ProblogLib.alias !in libraries) {
            libraries.plus(ProblogLib.MinimalBuiltins)
        } else {
            libraries
        }

    /** Same as [solverOf] (the [InputStore]/[OutputStore] overload), but takes individual channels. */
    override fun solverOf(
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
            ClassicSolverFactory.solverOf(
                unificator,
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings,
            ),
        )

    /** Same as [mutableSolverOf] (the [InputStore]/[OutputStore] overload), but takes individual channels. */
    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): MutableSolver =
        MutableProblogSolver(
            ClassicSolverFactory.mutableSolverOf(
                unificator,
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings,
            ),
        )

    /** Same as [SolverFactory.mutableSolverOf], but ensures [ProblogLib] is loaded (see [fixLibraries]),
     * forces [TrackVariables] `ON`, and rewrites [staticKb]/[dynamicKb] into a
     * [it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory] before handing everything to
     * [ClassicSolverFactory]. */
    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): MutableSolver =
        MutableProblogSolver(
            ClassicSolverFactory.mutableSolverOf(
                unificator,
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(unificator, staticKb),
                ProblogTheory.of(unificator, dynamicKb),
                inputs,
                outputs,
            ),
        )
}
