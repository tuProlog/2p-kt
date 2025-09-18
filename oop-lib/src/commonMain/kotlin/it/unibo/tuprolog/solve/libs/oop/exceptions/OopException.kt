package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.libs.oop.name
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

abstract class OopException : TuPrologException {
    @JvmOverloads
    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    abstract fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError

    protected abstract val culprit: Term

    companion object {
        internal fun List<Set<KClass<*>>>.pretty(): String = joinToString { it.pretty() }

        internal fun Set<KClass<*>>.pretty(): String = joinToString("|") { it.name }
    }
}
