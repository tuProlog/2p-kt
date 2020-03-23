package it.unibo.tuprolog.solve.libraries.exception

class AlreadyLoadedLibraryException(
    message: String? = null,
    cause: Throwable? = null
) : LibraryException(message, cause) {
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
