package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

/**
 * Implementation of primitive handling `halt/0` behaviour
 *
 * @author Enrico
 */
object Halt : PrimitiveWrapper<ExecutionContext>("halt", 0) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
        throw HaltException(context = request.context)
}
