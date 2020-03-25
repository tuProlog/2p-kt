package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

interface SolverFactory {

    val defaultLibraries: Libraries
        get() = Libraries()

    val defaultBuiltins: AliasedLibrary

    val defaultFlags: PrologFlags
        get() = emptyMap()

    val defaultStaticKB: ClauseDatabase
        get() = ClauseDatabase.empty()

    val defaultDynamicKB: ClauseDatabase
        get() = ClauseDatabase.empty()

    val defaultInputChannel: InputChannel<String>
        get() = InputChannel.stdIn()

    val defaultOutputChannel: OutputChannel<String>
        get() = OutputChannel.stdOut()

    val defaultErrorChannel: OutputChannel<String>
        get() = OutputChannel.stdErr()

    val defaultWarningsChannel: OutputChannel<PrologWarning>
        get() = OutputChannel.stdErr()

    fun solverOf(
        libraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKB: ClauseDatabase = defaultStaticKB,
        dynamicKB: ClauseDatabase = defaultDynamicKB,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): Solver

    fun solverWithDefaultBuiltins(
        otherLibraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKB: ClauseDatabase = defaultStaticKB,
        dynamicKB: ClauseDatabase = defaultDynamicKB,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): Solver =
        solverOf(
            otherLibraries + defaultBuiltins,
            flags,
            staticKB,
            dynamicKB,
            stdIn,
            stdOut,
            stdErr,
            warnings
        )
}