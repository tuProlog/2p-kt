package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implements ISO's `read_term/3`: like [ReadTerm2], but reading from the stream identified by the first argument
 * (an alias or `$stream(...)` term) instead of the current input.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the third argument is bound but not a list.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`read_option`) if an element of the third argument is
 * not one of the recognized option shapes.
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if the stream's next term is malformed Prolog syntax.
 */
object ReadTerm3 : TernaryRelation.NonBacktrackable<ExecutionContext>("read_term") {
    override val help: String =
        """
        `read_term(+Stream, ?Term, ?Options)`
        
        Reads the next term from the input stream `Stream` like `read/2`, and unifies the elements of the `Options` list with information about the term's variables. `Stream` must be an alias (e.g. `user_input`) or a `${'$'}stream(in, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an output stream a domain error (`stream_type`), where ISO prescribes a permission error. Each element of `Options` may be `variables(Vars)` (the variables of the term, in order of appearance), `variable_names(Names)` (a list of `'Name' = Var` pairs) or `singletons(Names)` (the same, for variables occurring once); other elements raise a domain error (`read_option`). Deviating from ISO, an unbound `Options` is not an error: it is unified with `[variables(Vars), variable_names(Names), singletons(Singletons)]`. Deviating from ISO, the current implementation fails, instead of unifying `Term` with `end_of_file`, when no more terms are available; it raises a syntax error if the next term is malformed and a system error if the channel is closed or cannot be read term by term.

        **Examples**

        ```prolog
        ?- read_term(user_input, T, [foo]).
        throws error(domain_error(read_option, foo), _).

        ?- read_term(S, T, []).
        throws error(instantiation_error, _).

        ?- read_term(user_output, T, []).
        throws error(domain_error(stream_type, user_output), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        return readTermAndReply(channel, second, lastIsInfoList = true)
    }
}
