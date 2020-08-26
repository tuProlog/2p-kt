@file:JvmName("SolverExtensions")
package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("classicSolver")
fun Solver.Companion.classic(
    libraries: Libraries = ClassicSolverFactory.defaultLibraries,
    flags: FlagStorage = ClassicSolverFactory.defaultFlags,
    staticKb: Theory = ClassicSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = ClassicSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicSolverFactory.defaultWarningsChannel
): Solver =
    ClassicSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicSolverWithDefaultBuiltins")
fun Solver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = ClassicSolverFactory.defaultLibraries,
    flags: FlagStorage = ClassicSolverFactory.defaultFlags,
    staticKb: Theory = ClassicSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicSolverFactory.defaultWarningsChannel
): Solver =
    ClassicSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolver")
fun MutableSolver.Companion.classic(
    libraries: Libraries = ClassicSolverFactory.defaultLibraries,
    flags: FlagStorage = ClassicSolverFactory.defaultFlags,
    staticKb: Theory = ClassicSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicSolverFactory.defaultWarningsChannel
): MutableSolver =
    ClassicSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolverWithDefaultBuiltins")
fun MutableSolver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = ClassicSolverFactory.defaultLibraries,
    flags: FlagStorage = ClassicSolverFactory.defaultFlags,
    staticKb: Theory = ClassicSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicSolverFactory.defaultWarningsChannel
): MutableSolver =
    ClassicSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)