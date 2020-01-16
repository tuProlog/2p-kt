package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.MessageError
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
                Throw.functor(1).run { to(MessageError::class) },
                Throw.functor("ciao").run { to(MessageError::class) },
                Throw.functor(ErrorUtils.errorStructOf(atomOf(SystemError.typeFunctor))).run { to(SystemError::class) }
            ).mapKeys { (query, _) -> createSolveRequest(query, primitives = mapOf(Throw.descriptionPair)) }
        }
    }
}
