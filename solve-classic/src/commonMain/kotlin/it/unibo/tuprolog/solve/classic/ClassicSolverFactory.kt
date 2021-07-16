package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory

object ClassicSolverFactory : SolverFactory {

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
        warnings: OutputChannel<Warning>
    ): Solver = ClassicSolver(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): MutableSolver = MutableClassicSolver(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
}
