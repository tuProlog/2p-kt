package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.streams.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsExport

/**
 * The [SolverFactory] for `:solve-streams`, an experimental, `Sequence`-based resolution engine.
 *
 * This is the factory that ultimately backs the deprecated `Solver.streams` accessor declared in `:solve`: that
 * accessor locates this object at runtime -- by fully-qualified class name on the JVM, by module lookup on JS --
 * rather than depending on `:solve-streams` directly, which is why this object must remain a top-level,
 * no-argument-constructible `object` named exactly `StreamsSolverFactory` in this package.
 *
 * Every [Solver] produced here resolves goals by unfolding a tree of immutable states, lazily, as a Kotlin
 * `Sequence`: each state's `behave()` yields its successor state(s) -- several of them at a choice point -- and
 * an internal `StateMachineExecutor` flattens that tree depth-first into the `Sequence<Solution>` returned by
 * [Solver.solve]. This differs from `:solve-classic`'s explicit, iterator-driven finite-state machine (see the
 * "state-machine" explanation page in the project documentation): there, backtracking is implemented via a
 * mutable choice-point stack that the solver replays on demand; here, each state carries its own immutable copy
 * of the execution context and backtracking simply falls out of how the lazy sequence is walked.
 *
 * __This engine is not production-ready.__ [mutableSolverOf] is unimplemented here -- it always throws
 * [NotImplementedError] -- and a large share of the standard predicates exercised by 2P-Kt's shared conformance
 * test suite are skipped for this engine in this module's tests (e.g. most of `assert`/`retract`, `findall`,
 * arithmetic and type-checking predicates), meaning their behaviour under this resolution strategy is currently
 * untested and not guaranteed to be ISO-conformant. This is why `Solver.streams` is marked `@Deprecated` as
 * "experimental and not mature enough for general purpose usage". Prefer `Solver.prolog`
 * (`it.unibo.tuprolog.solve.classic.ClassicSolverFactory`) for anything beyond experimenting with this
 * alternative, more side-effect-free resolution style.
 *
 * @see it.unibo.tuprolog.solve.Solver.streams
 * @see SolverStrategies
 */
@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
object StreamsSolverFactory : SolverFactory {
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
    ): Solver = StreamsSolver(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs)

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
        StreamsSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            InputStore.fromStandard(stdIn),
            OutputStore.fromStandard(stdOut, stdErr, warnings),
        )

    /**
     * Always throws: the `:solve-streams` engine does not support [MutableSolver] yet.
     *
     * @throws NotImplementedError always.
     */
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
    ): MutableSolver {
        TODO("Mutable stream solver is not supported yet")
    }

    /**
     * Always throws: the `:solve-streams` engine does not support [MutableSolver] yet.
     *
     * @throws NotImplementedError always.
     */
    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): MutableSolver {
        TODO("Mutable stream solver is not supported yet")
    }
}
