@file:JvmName("SolverExtensions")
package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.jvm.JvmName

fun Solver.Companion.classic(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKb: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    ClassicSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun Solver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKb: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    ClassicSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.classic(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKb: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    ClassicSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKb: ClauseDatabase = ClauseDatabase.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    ClassicSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)