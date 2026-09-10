package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.SystemError

/**
 * A platform-level I/O failure (e.g. a missing file, an unreachable host, a write attempted on a read-only
 * resource), as raised by [it.unibo.tuprolog.solve.libs.io.Url.readAsText]/[it.unibo.tuprolog.solve.libs.io.Url.readAsByteArray]
 * and by the `openInputChannel`/`openOutputChannel` extension functions.
 *
 * Unlike [it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException], this exception carries no information
 * tying it to a specific predicate argument: it is always converted, via [toLogicError], into an uncaught
 * [it.unibo.tuprolog.solve.exception.error.SystemError] rather than one of the argument-indexed ISO errors (such as
 * `existence_error/2` or `permission_error/3`), since 2P-Kt does not attempt to classify the underlying platform
 * failure any further.
 */
class IOException : TuPrologException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    /** Converts this exception into a [LogicError], namely a [SystemError] wrapping it, to be thrown from a primitive. */
    fun toLogicError(context: ExecutionContext): LogicError = SystemError.forUncaughtException(context, this)
}
