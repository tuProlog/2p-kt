package it.unibo.tuprolog.bdd.exception

import kotlin.jvm.JvmOverloads

class DataStructureOperationException @JvmOverloads constructor(
    message: String?,
    cause: Throwable? = null
) : DataStructureException(message, cause)
