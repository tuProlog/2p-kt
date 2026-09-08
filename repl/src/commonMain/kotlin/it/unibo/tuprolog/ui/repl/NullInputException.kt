package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Signals that the REPL's standard input stream ended (returned `null`) while a query or a continuation
 * line was expected, e.g. because the terminal was closed or `stdin` reached end-of-file.
 *
 * [AbstractTuPrologCommand] throws this exception (from its `readQuery` helper) whenever `terminal.prompt(...)`
 * returns `null`; [TuPrologCmd] catches it to print a farewell message and terminate the REPL loop gracefully,
 * rather than crashing.
 */
class NullInputException : TuPrologException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : this(message, null)
}
