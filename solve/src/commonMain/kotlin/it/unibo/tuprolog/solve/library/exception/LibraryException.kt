package it.unibo.tuprolog.solve.library.exception

import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Base class for exceptions related to loading/unloading [it.unibo.tuprolog.solve.library.Library] instances into a
 * [it.unibo.tuprolog.solve.library.Runtime]. Unlike [it.unibo.tuprolog.solve.exception.ResolutionException], this
 * is a plain Kotlin exception, thrown by [it.unibo.tuprolog.solve.library.Runtime] mutating operations rather than
 * carried as a [it.unibo.tuprolog.solve.Solution.Halt].
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
open class LibraryException(
    message: String? = null,
    cause: Throwable? = null,
) : TuPrologException(message, cause) {
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
