package it.unibo.tuprolog.solve.solver.exception

import it.unibo.tuprolog.core.exception.TuprologException

/**
 * An exception thrown if there are problems during state machine execution
 *
 * TODO find if more detailed documentation is present in standard prolog
 *
 * @author Enrico
 */
internal class HaltException : TuprologException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
