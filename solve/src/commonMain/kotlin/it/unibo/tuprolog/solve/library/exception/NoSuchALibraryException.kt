package it.unibo.tuprolog.solve.library.exception

/**
 * Thrown by [it.unibo.tuprolog.solve.library.Runtime.minus] (any overload) -- see that method's KDoc for a caveat
 * about its actual (inverted) throw condition as currently implemented.
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
class NoSuchALibraryException(
    message: String? = null,
    cause: Throwable? = null,
) : LibraryException(message, cause) {
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
