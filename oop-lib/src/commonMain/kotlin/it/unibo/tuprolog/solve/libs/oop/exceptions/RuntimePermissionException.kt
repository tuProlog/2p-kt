package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.libs.oop.fullName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter

@Suppress("MemberVisibilityCanBePrivate")
class RuntimePermissionException(
    val callable: KCallable<*>,
    val receiver: Any?,
    cause: Throwable? = null
) : OopException(
    "Invoking method ${callable.pretty()}" + if (receiver != null) {
        " on object `$receiver` of type `${receiver::class.fullName} "
    } else {
        " "
    } + "is not permitted",
    cause
) {

    override fun toPrologError(
        context: ExecutionContext,
        signature: Signature
    ): PrologError {
        return PermissionError.of(
            context,
            signature,
            PermissionError.Operation.INVOKE,
            PermissionError.Permission.OOP_METHOD,
            culprit
        )
    }

    override val culprit: Term
        get() = Atom.of(
            if (receiver == null) {
                callable.pretty()
            } else {
                "${receiver::class.fullName}::${callable.pretty()}"
            }
        )

    companion object {

        private fun KClassifier?.pretty(): String =
            if (this is KClass<*>) {
                fullName
            } else {
                "$this"
            }

        private fun KParameter.pretty(): String =
            when (kind) {
                KParameter.Kind.INSTANCE -> "<this>"
                KParameter.Kind.EXTENSION_RECEIVER -> "<this>:${type.classifier.pretty()}"
                else -> "$name:${type.classifier}"
            }

        private fun KCallable<*>.pretty(): String =
            "$name(${parameters.map { it.pretty() }}): ${returnType.classifier.pretty()}"
    }
}
