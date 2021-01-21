package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface ProbSolverFactory {

    @JsName("defaultLibraries")
    val defaultLibraries: Libraries
        get() = Libraries.empty()

    @JsName("defaultBuiltins")
    val defaultBuiltins: AliasedLibrary

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
    val defaultWarningsChannel: OutputChannel<PrologWarning>
        get() = OutputChannel.warn()

    @JsName("solver")
    fun solverOf(
        libraries: Libraries = defaultLibraries,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): ProbSolver

    @JsName("solverOfLibrariesAndKBs")
    fun solverOf(
        libraries: Libraries,
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
        defaultLibraries,
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
        defaultLibraries,
        defaultFlags,
        staticKb,
        defaultStaticKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverOfLibrariesAndStaticKB")
    fun solverOf(
        libraries: Libraries,
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

    @JsName("solverOfLibraries")
    fun solverOf(
        libraries: Libraries
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
        defaultLibraries,
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
        otherLibraries: Libraries = defaultLibraries,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): ProbSolver = solverOf(
        otherLibraries + defaultBuiltins,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr,
        warnings
    )

    @JsName("solverWithDefaultBuiltinsAndLibrariesAndKBs")
    fun solverWithDefaultBuiltins(
        otherLibraries: Libraries,
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
        defaultLibraries,
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
        defaultLibraries,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("solverWithDefaultBuiltinsAndLibraries")
    fun solverWithDefaultBuiltins(
        otherLibraries: Libraries
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
        defaultLibraries,
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
        libraries: Libraries = defaultLibraries,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): MutableProbSolver

    @JsName("mutableSolverOfLibrariesAndKBs")
    fun mutableSolverOf(
        libraries: Libraries,
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
        defaultLibraries,
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
        defaultLibraries,
        defaultFlags,
        staticKb,
        defaultStaticKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverOfLibrariesAndStaticKB")
    fun mutableSolverOf(
        libraries: Libraries,
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

    @JsName("mutableSolverOfLibraries")
    fun mutableSolverOf(
        libraries: Libraries
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
        defaultLibraries,
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
        otherLibraries: Libraries = defaultLibraries,
        flags: FlagStore = defaultFlags,
        staticKb: Theory = defaultStaticKb,
        dynamicKb: Theory = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): MutableProbSolver =
        mutableSolverOf(
            otherLibraries + defaultBuiltins,
            flags,
            staticKb,
            dynamicKb,
            stdIn,
            stdOut,
            stdErr,
            warnings
        )

    @JsName("mutableSolverWithDefaultBuiltinsAndLibrariesAndKBs")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Libraries,
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
        defaultLibraries,
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
        defaultLibraries,
        defaultFlags,
        staticKb,
        defaultDynamicKb,
        defaultInputChannel,
        defaultOutputChannel,
        defaultErrorChannel,
        defaultWarningsChannel
    )

    @JsName("mutableSolverWithDefaultBuiltinsAndLibraries")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Libraries
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
            defaultLibraries,
            defaultFlags,
            defaultStaticKb,
            defaultDynamicKb,
            defaultInputChannel,
            defaultOutputChannel,
            defaultErrorChannel,
            defaultWarningsChannel
        )
}
