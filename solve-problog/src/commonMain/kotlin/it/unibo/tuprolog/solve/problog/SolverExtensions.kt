@file:JvmName("SolverExtensions")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("problogClassicSolver")
fun Solver.Companion.problogClassic(
    libraries: Runtime = ProblogSolverFactory.defaultRuntime,
    flags: FlagStore = ProblogSolverFactory.defaultFlags,
    staticKb: Theory = ProblogSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = ProblogSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = ProblogSolverFactory.defaultWarningsChannel
): Solver =
    ProblogSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicSolverWithDefaultBuiltins")
fun Solver.Companion.problogClassicWithDefaultBuiltins(
    libraries: Runtime = ProblogSolverFactory.defaultRuntime,
    flags: FlagStore = ProblogSolverFactory.defaultFlags,
    staticKb: Theory = ProblogSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = ProblogSolverFactory.defaultWarningsChannel
): Solver =
    ProblogSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolver")
fun MutableSolver.Companion.problogClassic(
    libraries: Runtime = ProblogSolverFactory.defaultRuntime,
    flags: FlagStore = ProblogSolverFactory.defaultFlags,
    staticKb: Theory = ProblogSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = ProblogSolverFactory.defaultWarningsChannel
): MutableSolver =
    ProblogSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolverWithDefaultBuiltins")
fun MutableSolver.Companion.problogClassicWithDefaultBuiltins(
    libraries: Runtime = ProblogSolverFactory.defaultRuntime,
    flags: FlagStore = ProblogSolverFactory.defaultFlags,
    staticKb: Theory = ProblogSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<Warning> = ProblogSolverFactory.defaultWarningsChannel
): MutableSolver =
    ProblogSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
