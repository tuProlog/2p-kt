@file:JvmName("SolverExtensions")
package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.jvm.JvmName

fun Solver.Companion.streams(
    libraries: Libraries = Libraries(),
    flags: FlagStorage = FlagStorage.EMPTY,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    StreamsSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun Solver.Companion.streamsWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: FlagStorage = FlagStorage.EMPTY,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    StreamsSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streams(
    libraries: Libraries = Libraries(),
    flags: FlagStorage = FlagStorage.EMPTY,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    StreamsSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streamsWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: FlagStorage = FlagStorage.EMPTY,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    StreamsSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)