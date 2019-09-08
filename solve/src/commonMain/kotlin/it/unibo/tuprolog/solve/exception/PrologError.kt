package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

// TODO: 08/09/2019 documentation
class PrologError : TuprologRuntimeException {

    constructor(type: Term, info: Term, context: ExecutionContext) : super(context) {
        this.type = type
        this.info = info
    }

    constructor(message: String?, type: Term, info: Term, context: ExecutionContext) : super(message, context) {
        this.type = type
        this.info = info
    }

    constructor(message: String?, cause: Throwable?, type: Term, info: Term, context: ExecutionContext) : super(message, cause, context) {
        this.type = type
        this.info = info
    }

    constructor(cause: Throwable?, type: Term, info: Term, context: ExecutionContext) : super(cause, context) {
        this.type = type
        this.info = info
    }

    val type: Term

    val info: Term

    val error: Struct
        get() = Struct.of("error", type, info)
}