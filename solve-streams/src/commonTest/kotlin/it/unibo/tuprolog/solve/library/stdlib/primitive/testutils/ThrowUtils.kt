package it.unibo.tuprolog.solve.library.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.library.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.exception.error.ErrorUtils
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [Throw]
 *
 * @author Enrico
 */
internal object ThrowUtils {

    /** Requests that will return exceptions, if primitive invoked */
    internal val errorThrowingBehaviourRequest by lazy {
        prolog {
            mapOf(
                Throw.functor("A").run { to(InstantiationError::class) },
                Throw.functor(1).run { to(SystemError::class) },
                Throw.functor("ciao").run { to(SystemError::class) },
                Throw.functor(ErrorUtils.errorStructOf(atomOf(SystemError.typeFunctor))).run { to(SystemError::class) },
                Throw.functor(ErrorUtils.errorStructOf("type_error"("integer", "ciao"))).run { to(TypeError::class) }
            ).mapKeys { (query, _) -> createSolveRequest(query, primitives = mapOf(Throw.descriptionPair)) }
        }
    }
}
