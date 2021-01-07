package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.IOMode
import it.unibo.tuprolog.solve.libs.io.openInputChannel
import it.unibo.tuprolog.solve.libs.io.openOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.PROPERTY_ALIAS_PATTERN
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensureTermIsSupportedProperty
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensureTermIsValidProperty
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsIOMode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsUrl
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object Open4 : QuaternaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term
    ): Solve.Response = open(third)

    private val Term?.alias: String?
        get() = ((this as? Struct)?.getArgAt(0) as? Atom)?.value

    fun Solve.Request<ExecutionContext>.open(
        third: Term
    ): Solve.Response {
        val url = ensuringArgumentIsUrl(0)
        val mode = ensuringArgumentIsIOMode(1)
        ensuringArgumentIsVariable(2)
        val options = if (arguments.size >= 4) {
            ensuringArgumentIsList(3)
            (arguments[3] as List).toSequence()
                .map { ensureTermIsValidProperty(it) }
                .map { ensureTermIsSupportedProperty(it) }
                .toSet()
        } else {
            emptySet()
        }
        val alias = options.firstOrNull { it matches PROPERTY_ALIAS_PATTERN }?.alias
        return when (mode) {
            IOMode.READ -> replyOpeningStream(url.openInputChannel(), third, alias)
            IOMode.WRITE -> replyOpeningStream(url.openOutputChannel(false), third, alias)
            IOMode.APPEND -> replyOpeningStream(url.openOutputChannel(true), third, alias)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: OutputChannel<String>,
        third: Term,
        alias: String? = null
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openOutputChannel(alias ?: "output_channel${channel.streamTerm[1]}", channel)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: InputChannel<String>,
        third: Term,
        alias: String? = null
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openInputChannel(alias ?: "input_channel${channel.streamTerm[1]}", channel)
        }
    }
}
