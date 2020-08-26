package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologWarning

class MissingPredicate(context: ExecutionContext, signature: Signature, cause: Throwable?) : PrologWarning(
    context = context,
    message = "No such a predicate: ${signature.toIndicator()}",
    cause = cause
) {
    constructor(context: ExecutionContext, signature: Signature) : this(context, signature, null)
}