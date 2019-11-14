package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [Throw]
 *
 * @author Enrico
 */
internal object ThrowUtils {

    private val aContext = DummyInstances.executionContext

    /** Requests that will throw exceptions directly, if primitive invoked */
    internal val exposedErrorThrowingBehaviourRequest by lazy {
        prolog {
            mapOf(
                    Throw.functor("A").run { to(InstantiationError::class) },
                    Throw.functor(1).run { to(SystemError::class) },
                    Throw.functor("ciao").run { to(SystemError::class) },
                    Throw.functor(ErrorUtils.errorStructOf(atomOf(SystemError.typeFunctor))).run { to(HaltException::class) }
            ).mapKeys { (query, _) -> createSolveRequest(query, primitives = mapOf(Throw.descriptionPair)) }
        }
    }

    /** Throw primitive working examples (when solved in normal resolution process) */
    internal val requestSolutionMap by lazy {
        prolog {
            mapOf(
                    Throw.functor(1).hasSolutions({
                        halt(HaltException(context = aContext,
                                cause = SystemError(context = aContext, extraData = numOf(1))
                        ))
                    }),
                    Throw.functor("X").hasSolutions({
                        halt(HaltException(context = aContext,
                                cause = with(InstantiationError(
                                        context = aContext,
                                        extraData = varOf("X")
                                )) {
                                    SystemError(context = aContext,
                                            cause = this,
                                            extraData = this.errorStruct
                                    )
                                }
                        ))
                    }),
                    Throw.functor("ciao").hasSolutions({
                        halt(HaltException(context = aContext,
                                cause = SystemError(context = aContext, extraData = Atom.of("ciao"))
                        ))
                    }),
                    Throw.functor(ErrorUtils.errorStructOf(atomOf(SystemError.typeFunctor))).hasSolutions({
                        halt(HaltException(context = aContext,
                                cause = SystemError(context = aContext)
                        ))
                    })
            ).mapKeys { (query, _) -> createSolveRequest(query, primitives = mapOf(Throw.descriptionPair)) }
        }
    }
}
