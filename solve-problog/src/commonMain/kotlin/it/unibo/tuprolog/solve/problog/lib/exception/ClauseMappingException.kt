package it.unibo.tuprolog.solve.problog.lib.exception

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.jvm.JvmOverloads

open class ClauseMappingException @JvmOverloads constructor(
    override val message: String?,
    cause: Throwable? = null
) : TuPrologException(cause)
