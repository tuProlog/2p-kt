package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * An exception thrown if time for execution finished before completion
 *
 * TODO find if more detailed documentation is present in standard prolog
 *
 * @author Enrico
 */
internal class TimeOutException : TuprologRuntimeException {
    val deltaTime: Long

    constructor(deltaTime: Long, context: ExecutionContext) : super(context) {
        this.deltaTime = deltaTime
    }

    constructor(message: String?, deltaTime: Long, context: ExecutionContext) : super(message, context) {
        this.deltaTime = deltaTime
    }

    constructor(message: String?, cause: Throwable?, deltaTime: Long, context: ExecutionContext) : super(message, cause, context) {
        this.deltaTime = deltaTime
    }

    constructor(cause: Throwable?, deltaTime: Long, context: ExecutionContext) : super(cause, context) {
        this.deltaTime = deltaTime
    }
}
