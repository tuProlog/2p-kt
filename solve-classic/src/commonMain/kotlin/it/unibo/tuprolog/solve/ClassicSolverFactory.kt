package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDERR
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDIN
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDOUT
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.WARNINGS
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.ClauseDatabase

object ClassicSolverFactory : SolverFactory {
    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver =
        ClassicSolver(
            libraries,
            flags,
            staticKB,
            dynamicKB,
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings)
        )

    fun solverWithDefaultBuiltins(
        otherLibraries: Libraries = defaultLibraries,
        flags: PrologFlags = defaultFlags,
        staticKB: ClauseDatabase = defaultStaticKB,
        dynamicKB: ClauseDatabase = defaultDynamicKB,
        stdIn: InputChannel<String> = defaultInputChannel,
        stdOut: OutputChannel<String> = defaultOutputChannel,
        stdErr: OutputChannel<String> = defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = defaultWarningsChannel
    ): Solver =
        ClassicSolver(
            otherLibraries + DefaultBuiltins,
            flags,
            staticKB,
            dynamicKB,
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings)
        )
}