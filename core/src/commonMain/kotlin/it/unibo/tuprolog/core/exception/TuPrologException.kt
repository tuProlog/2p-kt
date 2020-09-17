package it.unibo.tuprolog.core.exception

import kotlin.jvm.JvmOverloads

/**
 * Base class for all `tuProlog` related exceptions
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
open class TuPrologException @JvmOverloads constructor(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {

    // left this auxiliary constructor to initialize message when only cause is present
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
