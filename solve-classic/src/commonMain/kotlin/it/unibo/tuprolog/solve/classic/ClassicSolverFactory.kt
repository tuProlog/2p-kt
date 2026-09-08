package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsExport

/**
 * The [SolverFactory] for `:solve-classic`, 2P-Kt's ISO-standard, SLD-NF resolution engine.
 *
 * This is the factory that ultimately backs `Solver.prolog` (and its deprecated alias `Solver.classic`, both
 * declared in `:solve`): those accessors locate this object at runtime -- by fully-qualified class name on the
 * JVM, by module lookup on JS -- rather than depending on `:solve-classic` directly, which is why this object
 * must remain a top-level, no-argument-constructible `object` named exactly `ClassicSolverFactory` in this
 * package. Most callers should go through `Solver.prolog`/`Solver.classic` rather than referencing this object
 * directly; it is public mainly so those lookups, and the `it.unibo.tuprolog.solve.problog` and other resolution
 * strategies that build on top of the classic engine, have something to find and construct.
 *
 * Every [Solver]/[MutableSolver] produced here resolves goals with the explicit, inspectable finite-state
 * machine documented under `it.unibo.tuprolog.solve.classic.fsm` (see [ClassicExecutionContext] and
 * [SolutionIterator] for the two pieces that make it tick), rather than via the host language's call stack --
 * see the "state-machine" explanation page in the project documentation for the full rationale and its
 * grounding in Ciatto's 2021 paper on modelling a Prolog solver as a state machine.
 *
 * @see it.unibo.tuprolog.solve.Solver.prolog
 * @see AbstractClassicSolver
 */
@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
object ClassicSolverFactory : SolverFactory {
    override val defaultBuiltins: Library
        get() = DefaultBuiltins

    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): Solver = ClassicSolver(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs)

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
    ): Solver = ClassicSolver(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

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
        MutableClassicSolver(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): MutableSolver = MutableClassicSolver(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs)
}
