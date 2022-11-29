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

interface SolverFactory {

    @JsName("newBuilder")
    fun newBuilder(): SolverBuilder = SolverBuilderImpl(this)

    @JsName("defaultRuntime")
    val defaultRuntime: Runtime
        get() = Runtime.empty()

    @JsName("defaultBuiltins")
    val defaultBuiltins: Library

    @JsName("defaultUnificator")
    val defaultUnificator: Unificator
        get() = Unificator.default

    @JsName("defaultFlags")
    val defaultFlags: FlagStore
        get() = FlagStore.DEFAULT

    @JsName("defaultStaticKb")
    val defaultStaticKb: Theory
        get() = Theory.emptyIndexed(defaultUnificator)

    @JsName("defaultDynamicKb")
    val defaultDynamicKb: Theory
        get() = Theory.emptyIndexed(defaultUnificator)

    @JsName("defaultInputChannel")
    val defaultInputChannel: InputChannel<String>
        get() = InputChannel.stdIn()

    @JsName("defaultOutputChannel")
    val defaultOutputChannel: OutputChannel<String>
        get() = OutputChannel.stdOut()

    @JsName("defaultErrorChannel")
    val defaultErrorChannel: OutputChannel<String>
        get() = OutputChannel.stdErr()

    @JsName("defaultWarningsChannel")
    val defaultWarningsChannel: OutputChannel<Warning>
        get() = OutputChannel.warn()

    @JsName("rawSolverOf")
    fun solverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        inputs: InputStore = InputStore.fromStandard(defaultInputChannel),
        outputs: OutputStore = OutputStore.fromStandard(
            defaultOutputChannel,
            defaultErrorChannel,
            defaultWarningsChannel
        )
    ): Solver

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
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): Solver

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
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): Solver = solverOf(
        unificator,
        otherLibraries + defaultBuiltins,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr,
        warnings
    )

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
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): MutableSolver

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
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): MutableSolver = mutableSolverOf(
        unificator,
        otherLibraries + defaultBuiltins,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr,
        warnings
    )

    @JsName("rawMutableSolverOf")
    fun mutableSolverOf(
        unificator: Unificator = defaultUnificator,
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        inputs: InputStore = InputStore.fromStandard(defaultInputChannel),
        outputs: OutputStore = OutputStore.fromStandard(
            defaultOutputChannel,
            defaultErrorChannel,
            defaultWarningsChannel
        )
    ): MutableSolver
}
