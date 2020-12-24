@file:JvmName("ProbSolverExtensions")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.MutableProbSolver
import it.unibo.tuprolog.solve.ProbSolver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("problogClassicSolver")
fun ProbSolver.Companion.problogClassic(
    libraries: Libraries = ProblogProbSolverFactory.defaultLibraries,
    flags: FlagStore = ProblogProbSolverFactory.defaultFlags,
    staticKb: Theory = ProblogProbSolverFactory.defaultDynamicKb,
    dynamicKb: Theory = ProblogProbSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogProbSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogProbSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogProbSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ProblogProbSolverFactory.defaultWarningsChannel
): ProbSolver =
    ProblogProbSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicSolverWithDefaultBuiltins")
fun ProbSolver.Companion.problogClassicWithDefaultBuiltins(
    libraries: Libraries = ProblogProbSolverFactory.defaultLibraries,
    flags: FlagStore = ProblogProbSolverFactory.defaultFlags,
    staticKb: Theory = ProblogProbSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogProbSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogProbSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogProbSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogProbSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ProblogProbSolverFactory.defaultWarningsChannel
): ProbSolver =
    ProblogProbSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolver")
fun MutableProbSolver.Companion.problogClassic(
    libraries: Libraries = ProblogProbSolverFactory.defaultLibraries,
    flags: FlagStore = ProblogProbSolverFactory.defaultFlags,
    staticKb: Theory = ProblogProbSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogProbSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogProbSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogProbSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogProbSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ProblogProbSolverFactory.defaultWarningsChannel
): MutableProbSolver =
    ProblogProbSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolverWithDefaultBuiltins")
fun MutableProbSolver.Companion.problogClassicWithDefaultBuiltins(
    libraries: Libraries = ProblogProbSolverFactory.defaultLibraries,
    flags: FlagStore = ProblogProbSolverFactory.defaultFlags,
    staticKb: Theory = ProblogProbSolverFactory.defaultStaticKb,
    dynamicKb: Theory = ProblogProbSolverFactory.defaultDynamicKb,
    stdIn: InputChannel<String> = ProblogProbSolverFactory.defaultInputChannel,
    stdOut: OutputChannel<String> = ProblogProbSolverFactory.defaultOutputChannel,
    stdErr: OutputChannel<String> = ProblogProbSolverFactory.defaultErrorChannel,
    warnings: OutputChannel<PrologWarning> = ProblogProbSolverFactory.defaultWarningsChannel
): MutableProbSolver =
    ProblogProbSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
