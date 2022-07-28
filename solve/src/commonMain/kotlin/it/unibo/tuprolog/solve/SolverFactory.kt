package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface SolverFactory {

    @JsName("defaultRuntime")
    val defaultRuntime: Runtime
        get() = Runtime.empty()

    @JsName("defaultBuiltins")
    val defaultBuiltins: Library

    @JsName("defaultFlags")
    val defaultFlags: FlagStore
        get() = FlagStore.DEFAULT

    @JsName("defaultStaticKb")
    val defaultStaticKb: Theory
        get() = Theory.empty()

    @JsName("defaultDynamicKb")
    val defaultDynamicKb: Theory
        get() = Theory.empty()

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

    @JsName("solver")
    fun solverOf(
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): Solver

    @JsName("solverOfRuntimeAndKBs")
    fun solverOf(
        libraries: Runtime,
        staticKb: Theory,
        dynamicKb: Theory
    ) = solverOf(
        libraries,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOfKBs")
    fun solverOf(
        staticKb: Theory,
        dynamicKb: Theory
    ) = solverOf(
        defaultRuntime,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOfStaticKB")
    fun solverOf(
        staticKb: Theory
    ) = solverOf(
        defaultRuntime,
        defaultFlags,
        staticKb,
        defaultStaticKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOfRuntimeAndStaticKB")
    fun solverOf(
        libraries: Runtime,
        staticKb: Theory
    ) = solverOf(
        libraries,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOfRuntime")
    fun solverOf(
        libraries: Runtime
    ) = solverOf(
        libraries,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOf")
    fun solverOf() = solverOf(
        defaultRuntime,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltinsAnd")
    fun solverWithDefaultBuiltins(
        otherLibraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): Solver = solverOf(
        otherLibraries + defaultBuiltins,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr,
        warnings
    )

    @JsName("solverWithDefaultBuiltinsAndRuntimeAndKBs")
    fun solverWithDefaultBuiltins(
        otherLibraries: Runtime,
        staticKb: Theory,
        dynamicKb: Theory
    ) = solverWithDefaultBuiltins(
        otherLibraries,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltinsAndKBs")
    fun solverWithDefaultBuiltins(
        staticKb: Theory,
        dynamicKb: Theory
    ) = solverWithDefaultBuiltins(
        defaultRuntime,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltinsAndStaticKB")
    fun solverWithDefaultBuiltins(
        staticKb: Theory
    ) = solverWithDefaultBuiltins(
        defaultRuntime,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltinsAndRuntime")
    fun solverWithDefaultBuiltins(
        otherLibraries: Runtime
    ) = solverWithDefaultBuiltins(
        otherLibraries,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltins")
    fun solverWithDefaultBuiltins() = solverWithDefaultBuiltins(
        defaultRuntime,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolver")
    fun mutableSolverOf(
        libraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): MutableSolver

    @JsName("mutableSolverOfRuntimeAndKBs")
    fun mutableSolverOf(
        libraries: Runtime,
        staticKb: Theory,
        dynamicKb: Theory
    ) = mutableSolverOf(
        libraries,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOfKBs")
    fun mutableSolverOf(
        staticKb: Theory,
        dynamicKb: Theory
    ) = mutableSolverOf(
        defaultRuntime,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOfStaticKB")
    fun mutableSolverOf(
        staticKb: Theory
    ) = mutableSolverOf(
        defaultRuntime,
        defaultFlags,
        staticKb,
        defaultStaticKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOfRuntimeAndStaticKB")
    fun mutableSolverOf(
        libraries: Runtime,
        staticKb: Theory
    ) = mutableSolverOf(
        libraries,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOfRuntime")
    fun mutableSolverOf(
        libraries: Runtime
    ) = mutableSolverOf(
        libraries,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOf")
    fun mutableSolverOf() = mutableSolverOf(
        defaultRuntime,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltinsAnd")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Runtime = defaultRuntime,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<Warning> = defaultWarningsChannel
    ): MutableSolver =
        mutableSolverOf(otherLibraries + defaultBuiltins, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    @JsName("mutableSolverWithDefaultBuiltinsAndRuntimeAndKBs")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Runtime,
        staticKb: Theory,
        dynamicKb: Theory
    ) = mutableSolverWithDefaultBuiltins(
        otherLibraries,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltinsAndKBs")
    fun mutableSolverWithDefaultBuiltins(
        staticKb: Theory,
        dynamicKb: Theory
    ) = mutableSolverWithDefaultBuiltins(
        defaultRuntime,
        defaultFlags,
        staticKb,
        dynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltinsAndStaticKB")
    fun mutableSolverWithDefaultBuiltins(
        staticKb: Theory
    ) = mutableSolverWithDefaultBuiltins(
        defaultRuntime,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltinsAndRuntime")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Runtime
    ) = mutableSolverWithDefaultBuiltins(
        otherLibraries,
        defaultFlags,
        defaultStaticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltins")
    fun mutableSolverWithDefaultBuiltins() =
        mutableSolverWithDefaultBuiltins(
            defaultRuntime,
            defaultFlags,
            defaultStaticKb,
            defaultDynamicKb,
            defaultInputChannel,
            defaultOutputChannel,
            defaultErrorChannel,
            defaultWarningsChannel
        )
}
