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
    override val help: String =
        """
        `write_term(@Term, +Options)`
        
        Writes `Term` to the current output stream (standard output, unless changed via `set_output/1`), formatted according to `Options` and the current operators, succeeding deterministically. `Options` must be a list (an unbound `Options` raises a type error (`list`) rather than ISO's instantiation error) whose elements are `quoted(Bool)` (quote atoms and functors where needed), `ignore_ops(Bool)` (write operators in canonical functional notation) and `numbervars(Bool)` (write `'${'$'}VAR'(N)` terms as variable letters), with `Bool` being `true` or `false`; missing options default to `false`, and any other element raises a domain error (`write_option`). It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- write_term(f('A', 1 + 2), [quoted(true), ignore_ops(true)]).
        % prints: f('A', '+'(1, 2))
        yes.

        ?- write_term(f('A', 1 + 2), []).
        % prints: f(A, 1 + 2)
        yes.

        ?- write_term(a, [foo]).
        throws error(domain_error(write_option, foo), _).

        ?- write_term(a, Options).
        throws error(type_error(list, _), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val formatter = ensuringArgumentIsFormatter(1)
        return writeTermAndReply(currentOutputChannel, first, formatter)
    }
}
