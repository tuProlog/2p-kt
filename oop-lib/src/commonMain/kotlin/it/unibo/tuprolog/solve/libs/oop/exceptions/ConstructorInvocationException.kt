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
class ConstructorInvocationException(
    val type: KClass<*>,
    val admissibleTypes: List<Set<KClass<*>>>,
) : OopException(
        "There is no constructor on type ${type.fullName} which accepts " +
            "[${admissibleTypes.pretty()}] as formal arguments",
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        ExistenceError.of(
            context,
            ExistenceError.ObjectType.OOP_CONSTRUCTOR,
            culprit,
            message ?: "",
        )

    override val culprit: Term
        get() = Atom.of("${type.fullName}::(${admissibleTypes.pretty()})")
}
