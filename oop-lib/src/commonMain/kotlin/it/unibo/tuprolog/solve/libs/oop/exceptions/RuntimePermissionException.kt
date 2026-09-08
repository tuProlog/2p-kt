package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.libs.oop.fullName
import it.unibo.tuprolog.solve.libs.oop.pretty
import kotlin.reflect.KCallable

/**
 * Thrown when the platform's reflection facilities deny access to [callable] on [receiver] --
 * on the JVM, this wraps a `kotlin.reflect.full.IllegalCallableAccessException`, typically raised
 * when the JVM's own access-control checks (e.g. module boundaries, a `SecurityManager`) reject
 * an otherwise `public`, reflectively-resolved member.
 *
 * Surfaces to Prolog as a [it.unibo.tuprolog.solve.exception.error.PermissionError] with
 * operation [it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.INVOKE] and
 * permission [it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.OOP_METHOD].
 *
 * @param callable the member reflective access to which was denied.
 * @param receiver the instance [callable] was being invoked on, or `null` for a constructor,
 * static member, or companion-object member.
 */
@Suppress("MemberVisibilityCanBePrivate")
class RuntimePermissionException(
    val callable: KCallable<*>,
    val receiver: Any?,
    cause: Throwable? = null,
) : OopException(
        "Invoking method ${callable.pretty()}" +
            if (receiver != null) {
                " on object `$receiver` of type `${receiver::class.fullName} "
            } else {
                " "
            } + "is not permitted",
        cause,
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        PermissionError.of(
            context,
            signature,
            PermissionError.Operation.INVOKE,
            PermissionError.Permission.OOP_METHOD,
            culprit,
        )

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
