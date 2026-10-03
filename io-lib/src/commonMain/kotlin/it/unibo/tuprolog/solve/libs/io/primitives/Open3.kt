package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.open
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implements ISO's `open/3`: opens the source/sink named by the first argument (parsed into a
 * [it.unibo.tuprolog.solve.libs.io.Url], both proper URLs and bare filesystem paths are accepted) in the mode named
 * by the second (`read`, `write`, or `append`, see [it.unibo.tuprolog.solve.libs.io.IOMode]), registers the
 * resulting channel under an auto-generated alias, and unifies the third argument with its `$stream(...)` term.
 * Equivalent to `open/4` with an empty options list; see [Open4] for the full contract, including exceptions.
 *
 * ```prolog
 * ?- open('theory.pl', read, Stream), read(Stream, Term), close(Stream).
 * ```
 */
object Open3 : TernaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override val help: String =
        """
        `open(+SourceSink, +Mode, -Stream)`
        
        Opens `SourceSink`, an atom holding a URL or a plain file path, in `Mode` (`read`, `write` or `append`), registers the new stream under an auto-generated alias, and unifies `Stream` with its `${'$'}stream(Direction, Id)` term; it behaves as `open/4` with an empty options list. Any URL readable by the platform (e.g. files or `http(s)`) can be opened for reading, but the current implementation can only write or append to local files on the JVM, and never on JS. It raises an instantiation error if `SourceSink` or `Mode` is unbound, a domain error (`source_sink`) if `SourceSink` is not a valid URL or path, a type or domain error (`io_mode`) if `Mode` is not one of the allowed atoms, a type error (`variable`) if `Stream` is bound (ISO prescribes an uninstantiation error), and a system error if the resource cannot be opened.

        **Examples**

        ```prolog
        % with /path/to/data.pl containing `hello(world).`
        ?- open('/path/to/data.pl', read, S), read(S, T), close(S).
        T = hello(world).

        ?- open(F, read, S).
        throws error(instantiation_error, _).

        ?- open('/path/to/data.pl', update, S).
        throws error(domain_error(io_mode, update), _).

        ?- open('/path/to/data.pl', read, foo).
        throws error(type_error(variable, foo), _).
        ```
        """.trimIndent()

    override fun Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Response = open(third)
}
