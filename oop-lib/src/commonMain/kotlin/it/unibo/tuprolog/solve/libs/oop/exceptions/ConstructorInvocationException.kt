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
 * Thrown by [it.unibo.tuprolog.solve.libs.oop.OverloadSelector.findConstructor] (and, through it,
 * by [it.unibo.tuprolog.solve.libs.oop.TypeRef.create]) when [type] has no public constructor
 * accepting arguments whose types are compatible with [admissibleTypes] -- e.g. `new_object('java.util.ArrayList', [foo], X)`
 * fails this way, since no `ArrayList` constructor accepts an atom.
 *
 * Surfaces to Prolog as an [it.unibo.tuprolog.solve.exception.error.ExistenceError] of type
 * [it.unibo.tuprolog.solve.exception.error.ExistenceError.ObjectType.OOP_CONSTRUCTOR].
 *
 * @param type the type a constructor was sought on.
 * @param admissibleTypes, for each actual argument (in order), every JVM/Kotlin type it could
 * have been converted into.
 */
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
