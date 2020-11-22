package it.unibo.tuprolog.struct.exception

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.jvm.JvmOverloads

open class DataStructureException @JvmOverloads constructor(
        override val message: String?,
        cause: Throwable? = null
) : TuPrologException(cause)