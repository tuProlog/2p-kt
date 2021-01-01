package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.streams.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory

object StreamsSolverFactory : SolverFactory {
    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver =
        StreamsSolver(
            libraries,
            flags,
            staticKb,
            dynamicKb,
            InputStore.default(stdIn),
            OutputStore.default(stdOut, stdErr, warnings)
        )

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): MutableSolver {
        TODO("Not yet implemented")
    }
}
