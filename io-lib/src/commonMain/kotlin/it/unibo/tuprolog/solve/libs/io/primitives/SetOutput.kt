package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `set_output/1`: makes the stream identified by the argument (an alias or `$stream(...)` term)
 * the current output stream, i.e. the one implicitly used by unary predicates like `write/1`-family and
 * [PutChar1] (see [IOPrimitiveUtils.currentOutputChannel]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 */
object SetOutput : UnaryPredicate.NonBacktrackable<ExecutionContext>("set_output") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        return replySuccess {
            resetOutputChannels(context.outputChannels.setCurrent(channel))
        }
    }
}
