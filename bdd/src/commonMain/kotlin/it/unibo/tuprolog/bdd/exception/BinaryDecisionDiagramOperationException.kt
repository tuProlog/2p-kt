package it.unibo.tuprolog.bdd.exception

import kotlin.jvm.JvmOverloads

/**
 * Base class for all exceptions related to Binary Decision Diagrams operations
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
class BinaryDecisionDiagramOperationException @JvmOverloads constructor(
    message: String?,
    cause: Throwable? = null
) : BinaryDecisionDiagramException(message, cause)
