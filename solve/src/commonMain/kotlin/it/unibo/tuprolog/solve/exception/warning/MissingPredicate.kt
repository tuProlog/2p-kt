package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName

/**
 * A [Warning] reported when a goal invokes [signature] and no such predicate exists, but the `unknown` Prolog flag
 * (see `it.unibo.tuprolog.solve.flags.Unknown`) is set to warn rather than raise an
 * [it.unibo.tuprolog.solve.exception.error.ExistenceError] or fail silently.
 */
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
