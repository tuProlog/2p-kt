package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

/**
 * Implements predicate `sleep(+N)` where `N` must be instantiated as an integer.
 * The predicate execution always succeeds, unless the resolution process is halted because of a [TimeOutException].
 * Furthermore, the resolution of a `sleep(N)` sub-goal is guaranteed to require at least `N` milliseconds
 */
object Sleep : PrimitiveWrapper<ExecutionContext>("sleep", 1) {
    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
        sequence {
            request.ensuringAllArgumentsAreInstantiated()
                .ensuringArgumentIsInteger(0).let {
                    val initialTime = currentTimeInstant()
                    val threshold = request.arguments[0].castTo<Integer>().intValue.toLongExact()
                    while (currentTimeInstant() - initialTime < threshold);
                    yield(request.replySuccess())
                }
        }
}