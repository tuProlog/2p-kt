package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDERR
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDIN
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDOUT
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.WARNINGS
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory

object ClassicSolverFactory : SolverFactory {

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver =
        ClassicSolver(
            libraries,
            flags,
            staticKb,
            dynamicKb,
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings)
        )

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): MutableSolver =
        MutableClassicSolver(
            libraries,
            flags,
            staticKb,
            dynamicKb,
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings)
        )
}