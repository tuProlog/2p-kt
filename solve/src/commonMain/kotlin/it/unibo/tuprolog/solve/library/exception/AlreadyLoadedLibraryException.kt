package it.unibo.tuprolog.solve.library.exception

/**
 * Thrown by [it.unibo.tuprolog.solve.library.Runtime.plus] (either overload) when the `Runtime` being extended
 * already contains a library aliased the same as the one being added.
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
class AlreadyLoadedLibraryException(
    message: String? = null,
    cause: Throwable? = null,
) : LibraryException(message, cause) {
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
