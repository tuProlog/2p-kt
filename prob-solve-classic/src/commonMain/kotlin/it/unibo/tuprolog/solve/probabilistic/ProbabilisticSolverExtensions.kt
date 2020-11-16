@file:JvmName("ProbabilisticSolverExtensions")

package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("classicSolver")
fun ProbabilisticSolver.Companion.classic(
    libraries: Libraries = ClassicProbabilisticSolverFactory.defaultLibraries,
    flags: FlagStore = ClassicProbabilisticSolverFactory.defaultFlags,
    staticKb: Theory = ClassicProbabilisticSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = ClassicProbabilisticSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicProbabilisticSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicProbabilisticSolverFactory.defaultWarningsChannel
): ProbabilisticSolver =
    ClassicProbabilisticSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicSolverWithDefaultBuiltins")
fun ProbabilisticSolver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = ClassicProbabilisticSolverFactory.defaultLibraries,
    flags: FlagStore = ClassicProbabilisticSolverFactory.defaultFlags,
    staticKb: Theory = ClassicProbabilisticSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicProbabilisticSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicProbabilisticSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicProbabilisticSolverFactory.defaultWarningsChannel
): ProbabilisticSolver =
    ClassicProbabilisticSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolver")
fun MutableProbabilisticSolver.Companion.classic(
    libraries: Libraries = ClassicProbabilisticSolverFactory.defaultLibraries,
    flags: FlagStore = ClassicProbabilisticSolverFactory.defaultFlags,
    staticKb: Theory = ClassicProbabilisticSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicProbabilisticSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicProbabilisticSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicProbabilisticSolverFactory.defaultWarningsChannel
): MutableProbabilisticSolver =
    ClassicProbabilisticSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("classicMutableSolverWithDefaultBuiltins")
fun MutableProbabilisticSolver.Companion.classicWithDefaultBuiltins(
    libraries: Libraries = ClassicProbabilisticSolverFactory.defaultLibraries,
    flags: FlagStore = ClassicProbabilisticSolverFactory.defaultFlags,
    staticKb: Theory = ClassicProbabilisticSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ClassicProbabilisticSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ClassicProbabilisticSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ClassicProbabilisticSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ClassicProbabilisticSolverFactory.defaultWarningsChannel
): MutableProbabilisticSolver =
    ClassicProbabilisticSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
