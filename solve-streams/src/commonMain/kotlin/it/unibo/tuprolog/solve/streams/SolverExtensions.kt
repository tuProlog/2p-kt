@file:JvmName("SolverExtensions")

package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import kotlin.jvm.JvmName

fun Solver.Companion.streams(
    libraries: Runtime = StreamsSolverFactory.defaultRuntime,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = StreamsSolverFactory.defaultWarningsChannel
): Solver =
    StreamsSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun Solver.Companion.streamsWithDefaultBuiltins(
    libraries: Runtime = StreamsSolverFactory.defaultRuntime,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = StreamsSolverFactory.defaultWarningsChannel
): Solver =
    StreamsSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streams(
    libraries: Runtime = StreamsSolverFactory.defaultRuntime,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = StreamsSolverFactory.defaultWarningsChannel
): MutableSolver =
    StreamsSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

fun MutableSolver.Companion.streamsWithDefaultBuiltins(
    libraries: Runtime = StreamsSolverFactory.defaultRuntime,
    flags: FlagStore = StreamsSolverFactory.defaultFlags,
    staticKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = StreamsSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = StreamsSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = StreamsSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = StreamsSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = StreamsSolverFactory.defaultWarningsChannel
): MutableSolver =
    StreamsSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
