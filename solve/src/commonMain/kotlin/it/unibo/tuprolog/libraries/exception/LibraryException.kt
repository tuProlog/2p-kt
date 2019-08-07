package it.unibo.tuprolog.libraries.exception

import it.unibo.tuprolog.core.TuprologException

open class LibraryException : TuprologException {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}