package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.open
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `open/4`: like [Open3], but with an explicit fourth argument listing `stream_property/2` options
 * (currently only `alias(Name)` has any effect, registering the new channel under `Name` in addition to an
 * auto-generated alias; other recognized-but-fixed properties, e.g. `type(text)`, must match this implementation's
 * only supported value).
 *
 * ```prolog
 * ?- open('out.txt', write, Stream, [alias(myout)]), write(myout, hello), close(myout).
 * ```
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first, second or fourth argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`source_sink`) if the first argument does not parse
 * into a valid [it.unibo.tuprolog.solve.libs.io.Url].
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`io_mode`) if the second argument is not `read`,
 * `write` or `append`.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_property`) if an element of the options list
 * is not a `stream_property/2` shape.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError if an element of the options list is a
 * `stream_property/2` shape this implementation does not actually support (e.g. `type(binary)`), or if the resource
 * cannot be opened (e.g. missing file, or writing attempted on a non-file [it.unibo.tuprolog.solve.libs.io.Url]).
 */
object Open4 : QuaternaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override val help: String =
        """
        `open(+SourceSink, +Mode, -Stream, +Options)`
        
        Like `open/3`, opens `SourceSink` (a URL or file path atom) in `Mode` (`read`, `write` or `append`) and unifies `Stream` with the new `${'$'}stream(Direction, Id)` term, but also takes a list of `Options`. In the current implementation only `alias(Name)` has an effect, registering the stream under `Name` instead of an auto-generated alias; `input`, `output`, `type(text)`, `eof_action(eof_code)` and `reposition(false)` are accepted and ignored, other values of `type/1`, `eof_action/1` and `reposition/1` (e.g. `type(binary)`) raise a system error, and any other element raises a domain error (`stream_property`). `Options` must be a list (type error otherwise, even when unbound), and all other arguments are checked and raise errors exactly as for `open/3`.

        **Examples**

        ```prolog
        ?- open('/path/to/out.txt', write, _, [alias(out)]), write(out, hello), close(out).
        yes.

        ?- open('/path/to/out.txt', write, S, [type(binary)]).
        throws error(system_error, _).

        ?- open('/path/to/out.txt', write, S, [foo]).
        throws error(domain_error(stream_property, foo), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
    ): Solve.Response = open(third)
}
