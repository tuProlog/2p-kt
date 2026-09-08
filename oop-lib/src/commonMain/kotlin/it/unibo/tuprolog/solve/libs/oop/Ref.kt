package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

/**
 * A Prolog [Atom] that also acts as a reflective handle onto some JVM/Kotlin entity -- either a
 * live object ([ObjectRef]) or a type ([TypeRef]) -- letting Prolog code invoke members and
 * assign properties on it without a hand-written [it.unibo.tuprolog.solve.primitive.Primitive]
 * for each one.
 *
 * This is the core abstraction `:oop-lib` uses to expose reflection to Prolog: predicates such as
 * `invoke_method/3` ([it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod]) and `assign/3`
 * ([it.unibo.tuprolog.solve.libs.oop.primitives.Assign]) simply delegate to [invoke] and [assign]
 * on whichever [Ref] they are given.
 *
 * @see ObjectRef
 * @see TypeRef
 */
interface Ref : Atom {
    /**
     * Invokes the (possibly overloaded) member named [methodName] on this reference, converting
     * [arguments] to actual JVM/Kotlin values via [objectConverter] and picking the overload whose
     * formal parameters best match them.
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.MethodInvocationException if no member
     * named [methodName] accepts arguments compatible with [arguments].
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException if the invoked
     * member throws.
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException if reflective
     * access to the member is denied by the runtime.
     */
    fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        vararg arguments: Term,
    ): Result = invoke(objectConverter, methodName, listOf(*arguments))

    /** @see invoke */
    fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: List<Term>,
    ): Result

    /** @see invoke */
    fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: Iterable<Term>,
    ): Result = invoke(objectConverter, methodName, arguments.toList())

    /** @see invoke */
    fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: Sequence<Term>,
    ): Result = invoke(objectConverter, methodName, arguments.toList())

    /** Like [invoke], but converting [arguments] via [TermToObjectConverter.default]. */
    fun invoke(
        methodName: String,
        vararg arguments: Term,
    ): Result = invoke(TermToObjectConverter.default, methodName, listOf(*arguments))

    /** @see invoke */
    fun invoke(
        methodName: String,
        arguments: List<Term>,
    ): Result = invoke(TermToObjectConverter.default, methodName, arguments)

    /** @see invoke */
    fun invoke(
        methodName: String,
        arguments: Iterable<Term>,
    ): Result = invoke(TermToObjectConverter.default, methodName, arguments.toList())

    /** @see invoke */
    fun invoke(
        methodName: String,
        arguments: Sequence<Term>,
    ): Result = invoke(TermToObjectConverter.default, methodName, arguments.toList())

    /**
     * Assigns [value] (converted via [objectConverter]) to the mutable property named
     * [propertyName] on this reference, selecting the overload whose type best matches [value].
     *
     * @return `true` if the assignment was performed.
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.PropertyAssignmentException if no
     * mutable property named [propertyName] accepts a value compatible with [value].
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException if the underlying
     * setter throws.
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException if reflective
     * access to the setter is denied by the runtime.
     */
    fun assign(
        objectConverter: TermToObjectConverter,
        propertyName: String,
        value: Term,
    ): Boolean

    /** Like [assign], but converting [value] via [TermToObjectConverter.default]. */
    fun assign(
        propertyName: String,
        value: Term,
    ): Boolean = assign(TermToObjectConverter.default, propertyName, value)
}
