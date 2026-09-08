package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.impl.SolverBuilderImpl
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * A factory for a specific [Solver] implementation (i.e. a specific resolution strategy), providing both defaults
 * for every piece of a solver's state and constructors for [Solver]/[MutableSolver] instances.
 *
 * Each resolution strategy module (`:solve-classic`, `:solve-streams`, `:solve-concurrent`, `:solve-problog`)
 * provides its own [SolverFactory] implementation, reachable from [Solver]'s companion object (e.g. [Solver.prolog]).
 * All factories are interchangeable from client code's perspective, since they all produce the same [Solver]
 * interface.
 *
 * @see Solver
 * @see SolverBuilder
 */
interface SolverFactory {
    /** Returns a new, mutable [SolverBuilder] seeded with this factory's defaults, for fluent, stepwise construction. */
    @JsName("newBuilder")
    fun newBuilder(): SolverBuilder = SolverBuilderImpl(this)

    /** The default `Runtime` of loaded libraries for solvers created by this factory; empty unless overridden. */
    @JsName("defaultRuntime")
    val defaultRuntime: Runtime
        get() = Runtime.empty()

    /** The standard-library [Library] this implementation ships with (see `defaultBuiltins` usages in `...WithDefaultBuiltins` methods). */
    @JsName("defaultBuiltins")
    val defaultBuiltins: Library

    /** The default [Unificator] for solvers created by this factory. */
    @JsName("defaultUnificator")
    val defaultUnificator: Unificator
        get() = Unificator.default

    /** The default [FlagStore] for solvers created by this factory. */
    @JsName("defaultFlags")
    val defaultFlags: FlagStore
        get() = FlagStore.DEFAULT

    /** The default (empty) static knowledge base for solvers created by this factory. */
    @JsName("defaultStaticKb")
    val defaultStaticKb: Theory
        get() = Theory.emptyIndexed(defaultUnificator)

    /** The default (empty) dynamic knowledge base for solvers created by this factory. */
    @JsName("defaultDynamicKb")
    val defaultDynamicKb: Theory
        get() = Theory.emptyIndexed(defaultUnificator)

    /** The default standard input channel for solvers created by this factory. */
    @JsName("defaultInputChannel")
    val defaultInputChannel: InputChannel<String>
        get() = InputChannel.stdIn()

    /** The default standard output channel for solvers created by this factory. */
    @JsName("defaultOutputChannel")
    val defaultOutputChannel: OutputChannel<String>
        get() = OutputChannel.stdOut()

    /** The default standard error channel for solvers created by this factory. */
    @JsName("defaultErrorChannel")
    val defaultErrorChannel: OutputChannel<String>
        get() = OutputChannel.stdErr()

    /** The default warnings channel for solvers created by this factory. */
    @JsName("defaultWarningsChannel")
    val defaultWarningsChannel: OutputChannel<Warning>
        get() = OutputChannel.warn()

    /** Creates a new [Solver], with an explicit [InputStore]/[OutputStore] rather than individual channels. */
    @JsName("rawSolverOf")
    fun solverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        inputs: InputStore = InputStore.fromStandard(defaultInputChannel),
        outputs: OutputStore =
            OutputStore.fromStandard(
                defaultOutputChannel,
                defaultErrorChannel,
                defaultWarningsChannel,
            ),
    ): Solver

    /**
     * Creates a new [Solver] with this implementation's resolution strategy, defaulting every unspecified argument
     * to this factory's `default*` values. Does *not* include [defaultBuiltins]; see [solverWithDefaultBuiltins] for
     * that.
     */
    @JsName("solverOf")
    fun solverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel,
    ): Solver

    /** Same as [solverOf], but adds [defaultBuiltins] on top of [otherLibraries]. */
    @JsName("solverWithDefaultBuiltinsAnd")
    fun solverWithDefaultBuiltins(
        unificator: Unificator = defaultUnificator,
        otherLibraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel,
    ): Solver =
        solverOf(
            unificator,
            otherLibraries + defaultBuiltins,
            flags,
            staticKb,
            dynamicKb,
            stdIn,
            stdOut,
            stdErr,
            warnings,
        )

    /** Same as [solverOf], but returns a [MutableSolver]. Does *not* include [defaultBuiltins]. */
    @JsName("mutableSolverOf")
    fun mutableSolverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel,
    ): MutableSolver

    /** Same as [mutableSolverOf], but adds [defaultBuiltins] on top of [otherLibraries]. */
    @JsName("mutableSolverWithDefaultBuiltinsAnd")
    fun mutableSolverWithDefaultBuiltins(
        unificator: Unificator = defaultUnificator,
        otherLibraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel,
    ): MutableSolver =
        mutableSolverOf(
            unificator,
            otherLibraries + defaultBuiltins,
            flags,
            staticKb,
            dynamicKb,
            stdIn,
            stdOut,
            stdErr,
            warnings,
        )

    /** Same as `rawSolverOf`, but returns a [MutableSolver]. */
    @JsName("rawMutableSolverOf")
    fun mutableSolverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        inputs: InputStore = InputStore.fromStandard(defaultInputChannel),
        outputs: OutputStore =
            OutputStore.fromStandard(
                defaultOutputChannel,
                defaultErrorChannel,
                defaultWarningsChannel,
            ),
    ): MutableSolver
}
