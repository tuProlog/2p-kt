package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.js.JsName

interface SolverFactory {

    @JsName("defaultLibraries")
    val defaultLibraries: Libraries
        get() = Libraries()

    @JsName("defaultBuiltins")
    val defaultBuiltins: AliasedLibrary

    @JsName("defaultFlags")
    val defaultFlags: PrologFlags
        get() = emptyMap()

    @JsName("defaultStaticKb")
    val defaultStaticKb: ClauseDatabase
        get() = ClauseDatabase.empty()

    @JsName("defaultDynamicKb")
    val defaultDynamicKb: ClauseDatabase
        get() = ClauseDatabase.empty()

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
        get() = OutputChannel.stdErr()

    @JsName("solverOf")
    fun solverOf(
        libraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKb: ClauseDatabase = defaultStaticKb,
        dynamicKb: ClauseDatabase = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): Solver

    @JsName("solverWithDefaultBuiltins")
    fun solverWithDefaultBuiltins(
        otherLibraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKb: ClauseDatabase = defaultStaticKb,
        dynamicKb: ClauseDatabase = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): Solver = solverOf(
        otherLibraries + defaultBuiltins, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings
    )

    @JsName("mutableSolverOf")
    fun mutableSolverOf(
        libraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKb: ClauseDatabase = defaultStaticKb,
        dynamicKb: ClauseDatabase = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): MutableSolver

    @JsName("mutableSolverWithDefaultBuiltins")
    fun mutableSolverWithDefaultBuiltins(
        otherLibraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKb: ClauseDatabase = defaultStaticKb,
        dynamicKb: ClauseDatabase = defaultDynamicKb,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): MutableSolver =
        mutableSolverOf(otherLibraries + defaultBuiltins, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
}