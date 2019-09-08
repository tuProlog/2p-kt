package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.exception.TuprologException
import it.unibo.tuprolog.solve.ExecutionContext

// TODO add doc
open class TuprologRuntimeException : TuprologException {
    constructor(context: ExecutionContext) : super() {
        this.context = context
    }

    constructor(message: String?, context: ExecutionContext) : super(message) {
        this.context = context
    }

    constructor(message: String?, cause: Throwable?, context: ExecutionContext) : super(message, cause) {
        this.context = context
    }

    constructor(cause: Throwable?, context: ExecutionContext) : super(cause) {
        this.context = context
    }

    val context: ExecutionContext

//    val activationRecords: Sequence<ExecutionContext>
//        get() = this.context.pathToRoot.filter { it.isActivationRecord }
}