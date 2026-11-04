package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implements predicate `sleep(+N)` where `N` must be instantiated as an integer.
 * The predicate execution always succeeds, unless the resolution process is halted because of a [TimeOutException].
 * Furthermore, the resolution of a `sleep(N)` sub-goal is guaranteed to require at least `N` milliseconds (negative
 * values are treated as zero).
 *
 * Sleeping happens on the timeline of the resolution's chronometer (see [ExecutionContext.chronometer]), and never
 * outlives its deadline: if the timeout expires first, resolution halts right then, with a [TimeOutException]. On a
 * simulated timeline, sleeping just moves virtual time forward.
 */
object Sleep : UnaryPredicate<ExecutionContext>("sleep") {
    override val help: String =
        """
        `sleep(+Milliseconds)`
        
        Delays resolution for at least `Milliseconds` milliseconds, then succeeds. The argument must be an instantiated integer.

        **Examples**

        ```prolog
        ?- sleep(10).
        yes.

        ?- sleep(X).
        throws error(instantiation_error, _).

        ?- sleep(a).
        throws error(type_error(integer, a), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
        sequence {
            ensuringAllArgumentsAreInstantiated()
                .ensuringArgumentIsInteger(0)
                .let {
                    val duration =
                        arguments[0]
                            .castTo<Integer>()
                            .intValue
                            .toLongExact()
                            .milliseconds
                            .coerceAtLeast(Duration.ZERO)
                    if (context.chronometer.sleep(duration)) {
                        throw TimeOutException(context = context, exceededDuration = context.maxDuration)
                    }
                    yield(replySuccess())
                }
        }
}
