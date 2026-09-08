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

/**
 * Wraps whatever [Throwable] [callable] itself threw while being reflectively invoked on
 * [receiver] -- e.g. a `NullPointerException` from calling a method on a `null`-valued field, or
 * any other exception the invoked JVM/Kotlin code raises. On the JVM this unwraps the
 * `java.lang.reflect.InvocationTargetException` reflection wraps such failures in, so [cause] is
 * the original exception thrown by the invoked code, not the reflection machinery's own wrapper.
 *
 * Surfaces to Prolog as a [it.unibo.tuprolog.solve.exception.error.SystemError] for an uncaught
 * exception (see [it.unibo.tuprolog.solve.exception.error.SystemError.forUncaughtException]).
 *
 * @param callable the member whose invocation threw.
 * @param receiver the instance [callable] was being invoked on, or `null` for a constructor,
 * static member, or companion-object member.
 * @param inner the exception thrown by the invoked code.
 */
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
