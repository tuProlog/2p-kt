package it.unibo.tuprolog.core

open class TuprologRuntimeException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

open class InvalidClauseException(val term: Term, cause: Throwable? = null) : TuprologRuntimeException(cause) {
    override val message: String?
        get() = "Term `term` is not a valid clause"
}