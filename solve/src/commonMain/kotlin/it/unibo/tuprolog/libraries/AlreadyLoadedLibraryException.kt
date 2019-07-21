package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.TuprologException

class AlreadyLoadedLibraryException : LibraryException {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}