@file:JvmName("ProbSolverExtensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("problogClassicSolver")
fun ProbSolver.Companion.problogClassic(
        libraries: Libraries = ProblogClassicSolverFactory.defaultLibraries,
        flags: FlagStore = ProblogClassicSolverFactory.defaultFlags,
        staticKb: Theory = ProblogClassicSolverFactory.defaultDynamicKb,
        dynamicKb: Theory = ProblogClassicSolverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = ProblogClassicSolverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = ProblogClassicSolverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = ProblogClassicSolverFactory.defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = ProblogClassicSolverFactory.defaultWarningsChannel
): ProbSolver =
    ProblogClassicSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicSolverWithDefaultBuiltins")
fun ProbSolver.Companion.problogClassicWithDefaultBuiltins(
        libraries: Libraries = ProblogClassicSolverFactory.defaultLibraries,
        flags: FlagStore = ProblogClassicSolverFactory.defaultFlags,
        staticKb: Theory = ProblogClassicSolverFactory.defaultStaticKb,
        dynamicKb: Theory = ProblogClassicSolverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = ProblogClassicSolverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = ProblogClassicSolverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = ProblogClassicSolverFactory.defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = ProblogClassicSolverFactory.defaultWarningsChannel
): ProbSolver =
    ProblogClassicSolverFactory.solverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolver")
fun MutableProbSolver.Companion.problogClassic(
        libraries: Libraries = ProblogClassicSolverFactory.defaultLibraries,
        flags: FlagStore = ProblogClassicSolverFactory.defaultFlags,
        staticKb: Theory = ProblogClassicSolverFactory.defaultStaticKb,
        dynamicKb: Theory = ProblogClassicSolverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = ProblogClassicSolverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = ProblogClassicSolverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = ProblogClassicSolverFactory.defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = ProblogClassicSolverFactory.defaultWarningsChannel
): MutableProbSolver =
    ProblogClassicSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

@JsName("problogClassicMutableSolverWithDefaultBuiltins")
fun MutableProbSolver.Companion.problogClassicWithDefaultBuiltins(
        libraries: Libraries = ProblogClassicSolverFactory.defaultLibraries,
        flags: FlagStore = ProblogClassicSolverFactory.defaultFlags,
        staticKb: Theory = ProblogClassicSolverFactory.defaultStaticKb,
        dynamicKb: Theory = ProblogClassicSolverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = ProblogClassicSolverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = ProblogClassicSolverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = ProblogClassicSolverFactory.defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = ProblogClassicSolverFactory.defaultWarningsChannel
): MutableProbSolver =
    ProblogClassicSolverFactory.mutableSolverWithDefaultBuiltins(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
