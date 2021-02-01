package it.unibo.tuprolog.bdd.exception

import kotlin.jvm.JvmOverloads

/**
 * Base class for all exceptions related to Binary Decision Diagrams
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
open class BinaryDecisionDiagramException @JvmOverloads constructor(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {

    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
