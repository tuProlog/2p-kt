package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.Theory

object ProblogSolverFactory : SolverFactory {

    override val defaultBuiltins: Library
        get() = ProblogLib.DefaultBuiltins

    override val defaultFlags: FlagStore
        get() = ensureVariablesTracking(super.defaultFlags)

    private fun ensureVariablesTracking(flags: FlagStore): FlagStore =
        flags + TrackVariables { ON }

    override fun solverOf(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore
    ): Solver =
        ProblogSolver(
            ClassicSolverFactory.solverOf(
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
                inputs,
                outputs
            )
        )

    private fun fixLibraries(libraries: Runtime): Runtime {
        return if (ProblogLib.alias !in libraries) {
            libraries.plus(ProblogLib.MinimalBuiltins)
        } else {
            libraries
        }
    }

    override fun solverOf(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): Solver =
        ProblogSolver(
            ClassicSolverFactory.solverOf(
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings
            )
        )

    override fun mutableSolverOf(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): MutableSolver =
        MutableProblogSolver(
            ClassicSolverFactory.mutableSolverOf(
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings
            )
        )

    override fun mutableSolverOf(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore
    ): MutableSolver =
        MutableProblogSolver(
            ClassicSolverFactory.mutableSolverOf(
                fixLibraries(libraries),
                ensureVariablesTracking(flags),
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
                inputs,
                outputs
            )
        )
}
