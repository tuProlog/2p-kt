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
 * Thrown by [it.unibo.tuprolog.solve.libs.oop.OverloadSelector.findMethod] (and, through it, by
 * [it.unibo.tuprolog.solve.libs.oop.Ref.invoke]) when [type] has no public member named
 * [missingMethodName] accepting arguments whose types are compatible with [admissibleTypes] --
 * e.g. invoking `foo` on an object whose class declares no such method, or calling an existing
 * method with the wrong number/kind of arguments.
 *
 * Surfaces to Prolog as an [it.unibo.tuprolog.solve.exception.error.ExistenceError] of type
 * [it.unibo.tuprolog.solve.exception.error.ExistenceError.ObjectType.OOP_METHOD].
 *
 * @param type the type a method was sought on.
 * @param missingMethodName the method name that could not be resolved.
 * @param admissibleTypes, for each actual argument (in order), every JVM/Kotlin type it could
 * have been converted into.
 */
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
