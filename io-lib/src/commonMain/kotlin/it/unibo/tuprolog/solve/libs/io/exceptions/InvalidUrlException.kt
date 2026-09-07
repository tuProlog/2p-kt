package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.TypeError

/**
 * Signals that a string could not be parsed into a [it.unibo.tuprolog.solve.libs.io.Url], as raised internally by
 * [it.unibo.tuprolog.solve.libs.io.parseUrl] and [it.unibo.tuprolog.solve.libs.io.Url.Companion.of].
 *
 * Predicates accepting a source/sink argument (e.g. `consult/1`, `open/3,4`) catch this exception and convert it,
 * via [toLogicError], into an ISO `type_error(url, Culprit)` pointing at the offending argument.
 */
class InvalidUrlException : TuPrologException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    /** Converts this exception into a [LogicError], namely a `type_error(url, Culprit)` at argument [index] of [signature]. */
    fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
        culprit: Term,
        index: Int,
    ): LogicError = TypeError.forArgument(context, signature, TypeError.Expected.URL, culprit, index)
}
