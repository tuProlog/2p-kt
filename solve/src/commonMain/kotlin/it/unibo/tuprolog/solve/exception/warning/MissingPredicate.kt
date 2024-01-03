package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName

class MissingPredicate(
    cause: Throwable?,
    contexts: Array<ExecutionContext>,
    @JsName("signature") val signature: Signature,
) : Warning("No such a predicate: ${signature.toIndicator()}", cause, contexts) {
    constructor(
        cause: Throwable?,
        context: ExecutionContext,
        signature: Signature,
    ) : this(cause, arrayOf(context), signature)

    constructor(
        context: ExecutionContext,
        signature: Signature,
    ) : this(null, arrayOf(context), signature)

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): MissingPredicate = MissingPredicate(cause, contexts.setItem(index, newContext), signature)

    override fun updateLastContext(newContext: ExecutionContext): MissingPredicate =
        updateContext(
            newContext,
            contexts.lastIndex,
        )

    override fun pushContext(newContext: ExecutionContext): MissingPredicate =
        MissingPredicate(cause, contexts.addLast(newContext), signature)
}
