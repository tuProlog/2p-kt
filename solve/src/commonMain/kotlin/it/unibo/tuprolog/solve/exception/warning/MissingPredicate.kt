package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologWarning
import kotlin.js.JsName

class MissingPredicate(
    cause: Throwable?,
    contexts: Array<ExecutionContext>,
    @JsName("signature") val signature: Signature
) : PrologWarning("No such a predicate: ${signature.toIndicator()}", cause, contexts) {

    constructor(
        cause: Throwable?,
        context: ExecutionContext,
        signature: Signature
    ) : this(cause, arrayOf(context), signature)

    constructor(
        context: ExecutionContext,
        signature: Signature
    ) : this(null, arrayOf(context), signature)

    override fun updateContext(newContext: ExecutionContext): PrologWarning =
        MissingPredicate(cause, contexts.setFirst(newContext), signature)

    override fun pushContext(newContext: ExecutionContext): PrologWarning =
        MissingPredicate(cause, contexts.addLast(newContext), signature)
}
