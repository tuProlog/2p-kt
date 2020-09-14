@file:JvmName("SolverExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.jvm.JvmName

fun Solver.Companion.streams(
    libraries: Libraries = StreamsSolverFactory.defaultLibraries,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = StreamsSolverFactory.defaultWarningsChannel
): Solver =
    StreamsSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun Solver.Companion.streamsWithDefaultBuiltins(
    libraries: Libraries = StreamsSolverFactory.defaultLibraries,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = StreamsSolverFactory.defaultWarningsChannel
): Solver =
    StreamsSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streams(
    libraries: Libraries = StreamsSolverFactory.defaultLibraries,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = StreamsSolverFactory.defaultWarningsChannel
): MutableSolver =
    StreamsSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streamsWithDefaultBuiltins(
    libraries: Libraries = StreamsSolverFactory.defaultLibraries,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = StreamsSolverFactory.defaultWarningsChannel
): MutableSolver =
    StreamsSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
