package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.libs.oop.fullName
import kotlin.reflect.KClass

/**
 * Thrown by [it.unibo.tuprolog.solve.libs.oop.OverloadSelector.findProperty] (and, through it, by
 * [it.unibo.tuprolog.solve.libs.oop.Ref.assign]) when [type] has no public mutable property named
 * [missingPropertyName] whose setter accepts a value compatible with [admissibleTypes] -- e.g.
 * assigning to a read-only `val`, to a non-existent property, or to a property of an incompatible
 * type.
 *
 * Surfaces to Prolog as an [it.unibo.tuprolog.solve.exception.error.ExistenceError] of type
 * [it.unibo.tuprolog.solve.exception.error.ExistenceError.ObjectType.OOP_PROPERTY].
 *
 * @param type the type a property was sought on.
 * @param missingPropertyName the property name that could not be resolved.
 * @param admissibleTypes every JVM/Kotlin type the value being assigned could have been converted into.
 */
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
