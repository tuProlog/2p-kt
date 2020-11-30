package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogTheory
import it.unibo.tuprolog.solve.problogclassic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory

object ProblogClassicSolverFactory : ProbSolverFactory {

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
    ): ProbSolver =
        ProblogClassicSolver(
            libraries,
            flags,
            ProblogTheory(staticKb),
            ProblogTheory(dynamicKb),
            mapOf(ExecutionContextAware.STDIN to stdIn),
            mapOf(
                ExecutionContextAware.STDOUT to stdOut,
                ExecutionContextAware.STDERR to stdErr,
                ExecutionContextAware.WARNINGS to warnings
            )
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
    ): MutableProbSolver =
        MutableProblogClassicSolver(
            libraries,
            flags,
            ProblogTheory(staticKb),
            ProblogTheory(dynamicKb),
            mapOf(ExecutionContextAware.STDIN to stdIn),
            mapOf(
                ExecutionContextAware.STDOUT to stdOut,
                ExecutionContextAware.STDERR to stdErr,
                ExecutionContextAware.WARNINGS to warnings
            )
        )
}
