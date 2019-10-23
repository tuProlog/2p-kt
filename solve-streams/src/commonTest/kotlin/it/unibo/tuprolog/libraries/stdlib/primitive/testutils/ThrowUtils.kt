package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [Throw]
 *
 * @author Enrico
 */
internal object ThrowUtils {

    /** Requests that will throw exceptions directly, if primitive invoked */
    internal val exposedErrorThrowingBehaviourRequest by lazy {
        mapOf(
                Struct.of(Throw.functor, Var.of("A")).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)) to InstantiationError::class
                },
                Struct.of(Throw.functor, Integer.of(1)).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)) to SystemError::class
                },
                Struct.of(Throw.functor, Atom.of("ciao")).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)) to SystemError::class
                },
                Struct.of(Throw.functor, ErrorUtils.errorStructOf(Atom.of(SystemError.typeFunctor))).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)) to HaltException::class
                }
        )
    }

    /** Throw primitive working examples (when solved in normal resolution process) */
    internal val requestSolutionMap by lazy {
        mapOf(
                Struct.of(Throw.functor, Integer.of(1)).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = SystemError(
                                                context = this.context,
                                                extraData = Integer.of(1)
                                        )
                                ))
                        )
                    }
                },
                Struct.of(Throw.functor, Var.of("X")).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = SystemError(
                                                context = this.context,
                                                cause = InstantiationError(
                                                        context = this.context,
                                                        extraData = Var.of("X")
                                                ),
                                                extraData = InstantiationError(
                                                        context = this.context,
                                                        extraData = Var.of("X")
                                                ).errorStruct
                                        )
                                )))
                    }
                },
                Struct.of(Throw.functor, Atom.of("ciao")).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = SystemError(
                                                context = this.context,
                                                extraData = Atom.of("ciao")
                                        )
                                )))
                    }
                },
                Struct.of(Throw.functor, ErrorUtils.errorStructOf(Atom.of(SystemError.typeFunctor))).let {
                    createSolveRequest(it, primitives = mapOf(Throw.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = SystemError(context = this.context)
                                )))
                    }
                }
        )
    }
}
