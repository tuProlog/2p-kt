package it.unibo.tuprolog.bdd.exception

import kotlin.jvm.JvmOverloads

/**
 * Thrown when a [BinaryDecisionDiagram][it.unibo.tuprolog.bdd.BinaryDecisionDiagram]
 * operation fails. Every operator and utility function in the `:bdd` module
 * (e.g. `apply`, `and`, `or`, `not`, `expansion`, `map`, `any`, `toDotString`,
 * `countVariableNodes`) wraps its internal computation and rethrows any
 * failure as an instance of this class, so callers only need to catch a
 * single exception type regardless of what went wrong internally
 * (including exceptions thrown by user-supplied lambdas, such as the
 * `operator`/`predicate`/`mapper` callbacks passed to those functions).
 * The original failure is always available via [cause].
 *
 * @param message the detail message string.
 * @param cause the cause of this exception (e.g. the original exception
 * thrown by the failed operation or by a user-supplied callback).
 */
class BinaryDecisionDiagramOperationException
    @JvmOverloads
    constructor(
        message: String?,
        cause: Throwable? = null,
    ) : BinaryDecisionDiagramException(message, cause)
