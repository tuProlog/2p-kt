@file:JvmName("Refs")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmName

/*
 * Operations on the references of the core: invoking members of, and assigning properties of, the object an
 * [ObjectRef] wraps, or the static and companion-object members of the type a [TypeRef] wraps, through the first
 * [HostInteropBackend] supporting them. A [NullRef] has no members: operations on it throw a [NullPointerException].
 */

/** Invokes the member named [methodName] of the host value of this reference, with [arguments]. */
fun Ref.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
): Result = invoke(OOPLib.backends, objectConverter, methodName, arguments)

internal fun Ref.invoke(
    backends: List<HostInteropBackend>,
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
): Result {
    val receiver = receiver { "Cannot invoke $methodName on $this" }
    return backends.of(receiver).invoke(receiver, methodName, arguments, objectConverter)
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
): Boolean = assign(OOPLib.backends, objectConverter, propertyName, value)

internal fun Ref.assign(
    backends: List<HostInteropBackend>,
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
): Boolean {
    val receiver = receiver { "Cannot assign $propertyName on $this" }
    backends.of(receiver).assign(receiver, propertyName, value, objectConverter)
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
): Result = create(OOPLib.backends, objectConverter, arguments)

internal fun TypeRef.create(
    backends: List<HostInteropBackend>,
    objectConverter: TermToObjectConverter,
    arguments: List<Term>,
): Result = backends.of(this).create(this, arguments, objectConverter)

// null references have no members: they never reach a backend
private inline fun Ref.receiver(message: () -> String): ObjectRef =
    this as? ObjectRef ?: throw NullPointerException(message())

private fun List<HostInteropBackend>.of(receiver: ObjectRef): HostInteropBackend =
    firstOrNull { it.supports(receiver) } ?: platformBackend

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
