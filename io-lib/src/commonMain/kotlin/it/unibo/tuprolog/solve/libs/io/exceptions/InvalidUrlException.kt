package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.TypeError

class InvalidUrlException : TuPrologException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    fun toPrologError(
        context: ExecutionContext,
        signature: Signature,
        culprit: Term,
        index: Int
    ): PrologError {
        return TypeError.forArgument(context, signature, TypeError.Expected.URL, culprit, index)
    }
}
