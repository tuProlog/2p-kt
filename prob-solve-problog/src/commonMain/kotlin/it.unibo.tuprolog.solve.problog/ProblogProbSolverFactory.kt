package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.MutableProbSolver
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.ProbSolver
import it.unibo.tuprolog.solve.ProbSolverFactory
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.classic.classic
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.Theory

object ProblogProbSolverFactory : ProbSolverFactory {

    private object DefaultBuiltins : AliasedLibrary by ProblogLib {

        override val operators: OperatorSet by lazy {
            ProblogLib.operators + ClassicSolverFactory.defaultBuiltins.operators
        }

        override val theory: Theory by lazy {
            ProblogLib.theory + ClassicSolverFactory.defaultBuiltins.theory
        }

        override val primitives: Map<Signature, Primitive> by lazy {
            ProblogLib.primitives + ClassicSolverFactory.defaultBuiltins.primitives
        }
    }

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
        ProblogProbSolver(
            Solver.classic(
                libraries,
                flags,
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
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
    ): MutableProbSolver =
        MutableProblogProbSolver(
            MutableSolver.classic(
                libraries,
                flags,
                ProblogTheory.of(staticKb),
                ProblogTheory.of(dynamicKb),
                stdIn,
                stdOut,
                stdErr,
                warnings
            )
        )
}
