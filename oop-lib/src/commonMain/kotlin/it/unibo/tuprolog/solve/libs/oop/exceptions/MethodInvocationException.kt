package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.libs.oop.fullName
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
class MethodInvocationException(
    val type: KClass<*>,
    val missingMethodName: String,
    val admissibleTypes: List<Set<KClass<*>>>,
) : OopException(
        "There is no method on type ${type.fullName} which is named `$missingMethodName` and accepts " +
            "[${admissibleTypes.pretty()}] as formal arguments",
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        ExistenceError.of(
            context,
            ExistenceError.ObjectType.OOP_METHOD,
            culprit,
            message ?: "",
        )

    override val culprit: Term
        get() = Atom.of("${type.fullName}::$missingMethodName(${admissibleTypes.pretty()})")
}
