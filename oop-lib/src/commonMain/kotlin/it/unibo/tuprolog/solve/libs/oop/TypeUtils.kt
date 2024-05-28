@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.indexed
import kotlin.jvm.JvmName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty

private val PRIMITIVE_TYPES =
    setOf(
        Long::class,
        Int::class,
        Short::class,
        Byte::class,
        Char::class,
        Double::class,
        Float::class,
    )

internal const val ID = "[a-zA-Z_][a-zA-Z0-9_]*"

context(OOPContext)
val ObjectRef.oop: ObjectDecorator
    get() = ObjectDecorator.of(this, termificator, objectifier, typeFactory)

context(OOPContext)
val TypeRef.oop: TypeDecorator
    get() = TypeDecorator.of(this, termificator, objectifier, typeFactory)

expect val KClass<*>.companionObjectRef: Optional<out Any>

expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

internal expect fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T

expect fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>>

expect val KCallable<*>.formalParameterTypes: List<KClass<*>>

expect fun KCallable<*>.pretty(): String

expect fun <T> KCallable<T>.invoke(
    obj: Any?,
    vararg method: Any?,
): T

expect val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>

internal expect fun overloadSelector(
    type: KClass<*>,
    objectifier: Objectifier,
): OverloadSelector

val KClass<*>.isPrimitiveType: Boolean get() = this in PRIMITIVE_TYPES

infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean = isSupertypeOf(other, false)

fun KClass<*>.isSupertypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.allSupertypes(strict).any { it == this }

fun KClass<*>.superTypeDistance(other: KClass<*>): Int? =
    other.allSupertypes(
        false,
    ).indexed().firstOrNull { (_, it) -> it == this }?.index

infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean = isSubtypeOf(other, false)

fun KClass<*>.isSubtypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.isSupertypeOf(this, strict)

fun KClass<*>.subTypeDistance(other: KClass<*>): Int? = other.superTypeDistance(this)
