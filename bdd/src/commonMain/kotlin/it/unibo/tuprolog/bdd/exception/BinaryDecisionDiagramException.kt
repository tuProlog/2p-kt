package it.unibo.tuprolog.bdd.exception

import kotlin.jvm.JvmOverloads

/**
 * Base class for all exceptions related to Binary Decision Diagrams.
 * Currently, [BinaryDecisionDiagramOperationException] is the only
 * subclass raised by the `:bdd` module itself; this open class exists so
 * that callers wishing to catch any BDD-related failure (including ones
 * possibly introduced by future subclasses) can catch this common
 * supertype instead.
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
open class BinaryDecisionDiagramException
    @JvmOverloads
    constructor(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : RuntimeException(message, cause)
