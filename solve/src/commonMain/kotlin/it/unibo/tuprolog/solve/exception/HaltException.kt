package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * An exception thrown if there are problems during state machine execution
 *
 * TODO find if more detailed documentation is present in standard prolog
 *
 * @author Enrico
 */
internal class HaltException : TuprologRuntimeException {
    constructor(context: ExecutionContext) : super(context)

    constructor(message: String?, context: ExecutionContext) : super(message, context)

    constructor(message: String?, cause: Throwable?, context: ExecutionContext) : super(message, cause, context)

    constructor(cause: Throwable?, context: ExecutionContext) : super(cause, context)
}
