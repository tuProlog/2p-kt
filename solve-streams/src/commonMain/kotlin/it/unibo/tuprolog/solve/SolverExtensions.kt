@file:JvmName("SolverExtensions")
package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.jvm.JvmName

fun Solver.Companion.streams(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKB: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    StreamsSolverFactory.solverOf(libraries, flags, staticKB, dynamicKB, stdIn, stdOut, stdErr, warnings)

fun Solver.Companion.streamsWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKB: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    StreamsSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKB, dynamicKB, stdIn, stdOut, stdErr, warnings)