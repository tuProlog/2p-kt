package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements predicate `sleep(+N)` where `N` must be instantiated as an integer.
 * The predicate execution always succeeds, unless the resolution process is halted because of a [TimeOutException].
 * Furthermore, the resolution of a `sleep(N)` sub-goal is guaranteed to require at least `N` milliseconds
 */
object Sleep : UnaryPredicate<ExecutionContext>("sleep") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
        sequence {
            ensuringAllArgumentsAreInstantiated()
                .ensuringArgumentIsInteger(0)
                .let {
                    val initialTime = currentTimeInstant()
                    val threshold = arguments[0].castTo<Integer>().intValue.toLongExact()
                    while (currentTimeInstant() - initialTime < threshold) {
                        // busy waiting
                    }
                    yield(replySuccess())
                }
        }
}
