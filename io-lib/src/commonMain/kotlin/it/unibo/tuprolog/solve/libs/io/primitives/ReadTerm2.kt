package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readTermAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `read_term/2`: like [Read1], but the second argument is a list of options requesting term
 * metadata back (`variables(Vars)`, `variable_names(Pairs)`, `singletons(Pairs)`); if the list is unbound instead,
 * a `[Vars, Pairs, Singletons]` list is unified with it, reporting all three at once. See
 * [IOPrimitiveUtils.readTermAndReply] for the shared implementation.
 *
 * ```prolog
 * ?- read_term(Term, [variable_names(Names)]).
 * ```
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the options argument is bound but not a list.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`read_option`) if an element of the options list is
 * not one of the recognized shapes.
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if the stream's next term is malformed Prolog syntax.
 */
object ReadTerm2 : BinaryRelation.NonBacktrackable<ExecutionContext>("read_term") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = readTermAndReply(currentInputChannel, first, lastIsInfoList = true)
}
