package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.SystemError

class IOException : TuPrologException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    fun toPrologError(context: ExecutionContext): PrologError {
        return SystemError.forUncaughtException(context, this)
    }
}
