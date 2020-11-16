package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDERR
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDIN
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.STDOUT
import it.unibo.tuprolog.solve.ExecutionContextAware.Companion.WARNINGS
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.function.PrologFunction
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.probabilistic.representation.ProbabilisticRepresentationFactory
import it.unibo.tuprolog.solve.probabilistic.representation.ofProbLog
import it.unibo.tuprolog.solve.probabilistic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory

object ClassicProbabilisticSolverFactory : ProbabilisticSolverFactory {
    private val representationFactory = ProbabilisticRepresentationFactory.ofProbLog()

    private object DefaultLibrary : AliasedLibrary by representationFactory.defaultBuiltins {
        override val theory: Theory by lazy {
            representationFactory.defaultBuiltins.theory + DefaultBuiltins.theory
        }

        override val operators: OperatorSet by lazy {
            representationFactory.defaultBuiltins.operators + DefaultBuiltins.operators
        }

        override val functions: Map<Signature, PrologFunction> by lazy {
            representationFactory.defaultBuiltins.functions + DefaultBuiltins.functions
        }

        override val primitives: Map<Signature, Primitive> by lazy {
            representationFactory.defaultBuiltins.primitives + DefaultBuiltins.primitives
        }
    }

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultLibrary

    override fun solverOf(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): ProbabilisticSolver =
        ClassicProbabilisticSolver(
            libraries,
            flags,
            representationFactory.from(staticKb),
            representationFactory.from(dynamicKb),
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings),
            representationFactory,
            Solver.classic(
                libraries,
                flags,
                representationFactory.from(staticKb).toPrologTheory(),
                representationFactory.from(dynamicKb).toPrologTheory(),
                stdIn,
                stdOut,
                stdErr,
                warnings
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
    ): MutableProbabilisticSolver =
        MutableClassicProbabilisticSolver(
            libraries,
            flags,
            representationFactory.from(staticKb),
            representationFactory.from(dynamicKb),
            mapOf(STDIN to stdIn),
            mapOf(STDOUT to stdOut, STDERR to stdErr, WARNINGS to warnings),
            representationFactory,
            Solver.classic(
                libraries,
                flags,
                representationFactory.from(staticKb).toPrologTheory(),
                representationFactory.from(dynamicKb).toPrologTheory(),
                stdIn,
                stdOut,
                stdErr,
                warnings
            )
        )
}
