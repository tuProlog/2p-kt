package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsFormatter
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `write_term/2`: formats the first argument according to the options listed in the second
 * (`quoted(Bool)`, `ignore_ops(Bool)`, `numbervars(Bool)`, each defaulting to `false`, see
 * [IOPrimitiveUtils.ensuringArgumentIsFormatter]) and writes it to the current output stream (see
 * [IOPrimitiveUtils.currentOutputChannel]).
 *
 * ```prolog
 * ?- write_term(foo(X, 'a b'), [quoted(true)]).
 * ```
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the options argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if it is bound but not a list.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`write_option`) if an element of the list is not one
 * of the recognized option shapes.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object WriteTerm2 : BinaryRelation.NonBacktrackable<ExecutionContext>("write_term") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val formatter = ensuringArgumentIsFormatter(1)
        return writeTermAndReply(currentOutputChannel, first, formatter)
    }
}
