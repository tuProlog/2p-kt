package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.classic.classic
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.theory.Theory

object ProblogSolverFactory : SolverFactory {

    private object DefaultBuiltins : Library by ProblogLib {

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

    /* Minimum libraries always needed to solve queries */
    private object MinimumBuiltins : Library by ProblogLib {

        override val primitives: Map<Signature, Primitive> by lazy {
            ProblogLib.primitives + sequenceOf(
                EnsureExecutable
            ).map { it.descriptionPair }.toMap()
        }

        override val theory: Theory by lazy {
            ProblogLib.theory + Theory.indexedOf(
                sequenceOf(
                    Call
                ).map { it.implementation }
            )
        }
    }

    override val defaultBuiltins: Library
        get() = DefaultBuiltins

    private fun fixLibraries(libraries: Runtime): Runtime {
        return if (!libraries.containsValue(DefaultBuiltins)) {
            libraries.plus(MinimumBuiltins)
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
            Solver.classic(
                fixLibraries(libraries),
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
            MutableSolver.classic(
                fixLibraries(libraries),
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
