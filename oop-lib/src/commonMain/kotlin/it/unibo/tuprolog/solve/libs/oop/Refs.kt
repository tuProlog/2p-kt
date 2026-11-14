@file:JvmName("Refs")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmName

/*
 * Reflective operations on the references of the core: invoking members of, and assigning properties of, the object
 * an [ObjectRef] wraps, or the static and companion-object members of the type a [TypeRef] wraps. A [NullRef] has
 * no members: operations on it throw a [NullPointerException].
 */

/** Invokes the member named [methodName] of the host value of this reference, with [arguments]. */
fun Ref.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
): Result =
    when (this) {
        is TypeRef ->
            when (val companion = type.companionObjectRef) {
                is Optional.Some<out Any> -> companion.value.invoke(objectConverter, methodName, arguments)
                else -> type.invoke(objectConverter, methodName, arguments, null)
            }
        is ObjectRef -> value.invoke(objectConverter, methodName, arguments)
        else -> throw NullPointerException("Cannot invoke $methodName on $this")
    }

/** @see invoke */
fun Ref.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    vararg arguments: Term,
): Result = invoke(objectConverter, methodName, arguments.asList())

/** @see invoke */
fun Ref.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: Iterable<Term>,
): Result = invoke(objectConverter, methodName, arguments.toList())

/** @see invoke */
fun Ref.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: Sequence<Term>,
): Result = invoke(objectConverter, methodName, arguments.toList())

/** Invokes a member converting [arguments] with [TermToObjectConverter.default]. */
fun Ref.invoke(
    methodName: String,
    vararg arguments: Term,
): Result = invoke(TermToObjectConverter.default, methodName, arguments.asList())

/** @see invoke */
fun Ref.invoke(
    methodName: String,
    arguments: List<Term>,
): Result = invoke(TermToObjectConverter.default, methodName, arguments)

/** @see invoke */
fun Ref.invoke(
    methodName: String,
    arguments: Iterable<Term>,
): Result = invoke(TermToObjectConverter.default, methodName, arguments.toList())

/** @see invoke */
fun Ref.invoke(
    methodName: String,
    arguments: Sequence<Term>,
): Result = invoke(TermToObjectConverter.default, methodName, arguments.toList())

/** Assigns [value] to the mutable property named [propertyName] of the host value of this reference. */
fun Ref.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
): Boolean {
    when (this) {
        is TypeRef ->
            when (val companion = type.companionObjectRef) {
                is Optional.Some<out Any> -> companion.value.assign(objectConverter, propertyName, value)
                else -> type.assign(objectConverter, propertyName, value, null)
            }
        is ObjectRef -> this.value.assign(objectConverter, propertyName, value)
        else -> throw NullPointerException("Cannot assign $propertyName on $this")
    }
    return true
}

/** @see assign */
fun Ref.assign(
    propertyName: String,
    value: Term,
): Boolean = assign(TermToObjectConverter.default, propertyName, value)

/** Creates an instance of the type of this reference, through its constructor best matching [arguments]. */
fun TypeRef.create(
    objectConverter: TermToObjectConverter,
    arguments: List<Term>,
): Result = type.create(objectConverter, arguments)

/** @see create */
fun TypeRef.create(
    objectConverter: TermToObjectConverter,
    vararg arguments: Term,
): Result = create(objectConverter, arguments.asList())

/** @see create */
fun TypeRef.create(
    objectConverter: TermToObjectConverter,
    arguments: Iterable<Term>,
): Result = create(objectConverter, arguments.toList())

/** @see create */
fun TypeRef.create(
    objectConverter: TermToObjectConverter,
    arguments: Sequence<Term>,
): Result = create(objectConverter, arguments.toList())

/** @see create */
fun TypeRef.create(vararg arguments: Term): Result = create(TermToObjectConverter.default, arguments.asList())

/** @see create */
fun TypeRef.create(arguments: List<Term>): Result = create(TermToObjectConverter.default, arguments)

/** @see create */
fun TypeRef.create(arguments: Iterable<Term>): Result = create(TermToObjectConverter.default, arguments.toList())

/** @see create */
fun TypeRef.create(arguments: Sequence<Term>): Result = create(TermToObjectConverter.default, arguments.toList())
