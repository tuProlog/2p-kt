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
    override val help: String =
        """
        `read_term(?Term, ?Options)`
        
        Reads the next term from the current input stream (standard input, unless changed via `set_input/1`) like `read/1`, and unifies the elements of the `Options` list with information about the term's variables. Each element of `Options` may be `variables(Vars)` (the variables of the term, in order of appearance), `variable_names(Names)` (a list of `'Name' = Var` pairs) or `singletons(Names)` (the same, for variables occurring once); other elements raise a domain error (`read_option`). Deviating from ISO, an unbound `Options` is not an error: it is unified with `[variables(Vars), variable_names(Names), singletons(Singletons)]`. Deviating from ISO, the current implementation fails, instead of unifying `Term` with `end_of_file`, when no more terms are available; it raises a syntax error if the next term is malformed and a system error if the channel is closed or cannot be read term by term.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = readTermAndReply(currentInputChannel, first, lastIsInfoList = true)
}
