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
