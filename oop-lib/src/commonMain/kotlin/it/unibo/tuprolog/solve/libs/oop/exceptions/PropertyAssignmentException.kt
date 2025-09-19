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
class PropertyAssignmentException(
    val type: KClass<*>,
    val missingPropertyName: String,
    val admissibleTypes: Set<KClass<*>>,
) : OopException(
        "There is no property on type ${type.fullName} which is named `$missingPropertyName` and can be " +
            "assigned to a value of type ${admissibleTypes.pretty()}",
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        ExistenceError.of(
            context,
            ExistenceError.ObjectType.OOP_PROPERTY,
            culprit,
            message ?: "",
        )

    override val culprit: Term
        get() = Atom.of("${type.fullName}::$missingPropertyName: ${admissibleTypes.pretty()}")
}
