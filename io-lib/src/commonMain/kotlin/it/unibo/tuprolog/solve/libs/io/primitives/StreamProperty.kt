package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsStreamProperty
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrStream
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.propertiesOf
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object StreamProperty : BinaryRelation<ExecutionContext>("stream_property") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term, second: Term): Sequence<Solve.Response> {
        ensuringArgumentIsVarOrStream(0)
        ensuringArgumentIsStreamProperty(1)
        return sequenceOf(context.inputChannels, context.outputChannels)
            .flatMap { it.values.asSequence() }
            .distinct()
            .map { it.streamTerm to propertiesOf(it) }
            .flatMap { (streamTerm, properties) -> properties.map { streamTerm to it } }
            .map { (streamTerm, property) ->
                replyWith(mgu(first, streamTerm) + mgu(second, property))
            }
    }
}
