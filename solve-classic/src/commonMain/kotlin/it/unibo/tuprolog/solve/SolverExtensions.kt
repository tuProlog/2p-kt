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
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    ClassicSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicSolverWithDefaultBuiltins")
fun Solver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): Solver =
    ClassicSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolver")
fun MutableSolver.Companion.classic(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    ClassicSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolverWithDefaultBuiltins")
fun MutableSolver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    stdIn: InputChannel<String> = InputChannel.stdIn(),
    stdOut: OutputChannel<String> = OutputChannel.stdOut(),
    stdErr: OutputChannel<String> = OutputChannel.stdOut(),
    warnings: OutputChannel<PrologWarning> = OutputChannel.stdOut()
): MutableSolver =
    ClassicSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)