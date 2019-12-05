package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.exception.TimeOutException

object TestingPrimitives {

    /**
     * Implements predicate `sleep(+N)` where `N` must be instantiated as an integer.
     * The predicate execution always succeeds, unless the resolution process is halted because of a
     * [TimeOutException].
     * Furthermore, the resolution of a `sleep(N)` sub-goal is guaranteed to require at least `N` milliseconds
     */
    val sleep by lazy {
        object : PrimitiveWrapper<ExecutionContext>("sleep", 1) {
            override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
                sequence {
                    request.ensuringAllArgumentsAreInstantiated()
                        .ensuringArgumentsIsInteger(0).let {
                            val initialTime = currentTimeInstant()
                            val threshold = request.arguments[0].castTo<Integer>().intValue.toLongExact()
                            while (currentTimeInstant() - initialTime < threshold);
                            yield(request.replySuccess())
                        }
                }
        }
    }

    val timeLibrary = Library.of(alias = "prolog.time", primitives = mapOf(sleep.descriptionPair))
}