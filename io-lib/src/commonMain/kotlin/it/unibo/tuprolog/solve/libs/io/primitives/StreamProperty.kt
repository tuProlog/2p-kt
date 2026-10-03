package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsStreamProperty
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrStream
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.propertiesOf
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `stream_property/2`: enumerates, backtracking over every open channel (input and output alike),
 * the pairs of `$stream(...)` term and `stream_property/2` property that hold for it, unifying them respectively
 * with the first and second argument. Reported properties are `input`/`output`, one `alias(_)` per non-reserved
 * alias, and always `type(text)` (see [IOPrimitiveUtils.propertiesOf], since only text streams are supported).
 *
 * Both arguments act as filters when already bound: binding the first restricts enumeration to that stream, binding
 * the second (to `input`, `output`, or `alias(_)`) to that property.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_or_alias`) if the first argument is bound to
 * something other than a `$stream(...)` term.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_property`) if the second argument is bound
 * to something other than `input`, `output`, or `alias(_)`.
 */
object StreamProperty : BinaryRelation<ExecutionContext>("stream_property") {
    override val help: String =
        """
        `stream_property(?Stream, ?Property)`
        
        Enumerates, on backtracking, every open stream `Stream` (as a `${'$'}stream(Direction, Id)` term) together with each `Property` holding for it; the current implementation reports `input` or `output`, one `alias(Name)` per alias of the stream, and `type(text)`. If `Stream` is bound it must be a stream term (aliases are not accepted), otherwise a domain error (`stream_or_alias`) is raised, or an existence error if it denotes no open stream. If `Property` is bound it must be `input`, `output` or `alias(Name)`, otherwise a domain error (`stream_property`) is raised, so the reported `type(text)` property cannot be queried directly.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAll(
        first: Term,
        second: Term,
    ): Sequence<Solve.Response> {
        ensuringArgumentIsVarOrStream(0)
        ensuringArgumentIsStreamProperty(1)
        return sequenceOf(context.inputChannels, context.outputChannels)
            .flatMap { it.values.asSequence() }
            .distinct()
            .map { it.streamTerm to propertiesOf(it) }
            .flatMap { (streamTerm, properties) -> properties.map { streamTerm to it } }
            .map { (streamTerm, property) -> mgu(first, streamTerm) + mgu(second, property) }
            // Candidates that do not unify must be dropped rather than turned into a `No` response:
            // the FSM does not keep pulling from this sequence past the first non-matching element,
            // so leaving them in would make stream_property/2 only ever consider the very first
            // (stream, property) pair it enumerates.
            .filter { it.isSuccess }
            .map { replyWith(it) }
    }
}
