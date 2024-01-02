package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.libs.oop.fullName
import it.unibo.tuprolog.solve.libs.oop.pretty
import kotlin.reflect.KCallable

class OopRuntimeException(
    private val callable: KCallable<*>,
    private val receiver: Any?,
    inner: Throwable,
) : OopException(
        inner.message ?: "Unhandled exception of type ${inner::class.fullName} while executing OOP code",
        cause = inner,
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError = SystemError.forUncaughtException(context, this)

    override val message: String
        get() = super.message!!

    override val cause: Throwable
        get() = super.cause!!

    override val culprit: Term
        get() =
            Atom.of(
                if (receiver == null) {
                    callable.pretty()
                } else {
                    "${receiver::class.fullName}::${callable.pretty()}"
                },
            )
}
