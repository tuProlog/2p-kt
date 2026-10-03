package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `set_input/1`: makes the stream identified by the argument (an alias or `$stream(...)` term) the
 * current input stream, i.e. the one implicitly used by unary predicates like [Read1] and [GetChar1] (see
 * [IOPrimitiveUtils.currentInputChannel]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 */
object SetInput : UnaryPredicate.NonBacktrackable<ExecutionContext>("set_input") {
    override val help: String =
        """
        `set_input(+Stream)`
        
        Makes the input stream `Stream` the current input stream, i.e. the one used by the predicates without an explicit stream argument (e.g. `get_char/1`, `read/1`), succeeding deterministically. `Stream` must be an alias (e.g. `user_input`) or a `${'$'}stream(in, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an output stream a domain error (`stream_type`), where ISO prescribes a permission error.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        return replySuccess {
            resetInputChannels(context.inputChannels.setCurrent(channel))
        }
    }
}
