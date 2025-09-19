package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.SystemError

class IOException : TuPrologException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    fun toLogicError(context: ExecutionContext): LogicError = SystemError.forUncaughtException(context, this)
}
