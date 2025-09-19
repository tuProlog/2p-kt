package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implementation of primitive handling `halt/0` behaviour
 *
 * @author Enrico
 */
object Halt : PredicateWithoutArguments.NonBacktrackable<ExecutionContext>("halt") {
    override fun Solve.Request<ExecutionContext>.computeOne(): Solve.Response = throw HaltException(context = context)
}
