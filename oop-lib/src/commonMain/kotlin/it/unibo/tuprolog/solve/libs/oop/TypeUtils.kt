@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

expect val KClass<*>.name: String

expect val KClass<*>.fullName: String

expect fun KClass<*>.allSupertypes(strict: Boolean = false): Sequence<KClass<*>>

infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean =
    isSupertypeOf(other, false)

fun KClass<*>.isSupertypeOf(other: KClass<*>, strict: Boolean): Boolean =
    other.allSupertypes(strict).any { it == this }

infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean =
    isSubtypeOf(other, false)

fun KClass<*>.isSubtypeOf(other: KClass<*>, strict: Boolean): Boolean =
    other.isSupertypeOf(this, strict)

expect val KCallable<*>.formalParameterTypes: List<KClass<*>>

expect fun KClass<*>.findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*>

expect fun KClass<*>.findProperty(propertyName: String, admissibleTypes: Set<KClass<*>>): KMutableProperty<*>

expect fun KClass<*>.findConstructor(admissibleTypes: List<Set<KClass<*>>>): KCallable<*>

fun Any.invoke(methodName: String, arguments: List<Term>): Result =
    this::class.invoke(methodName, arguments, this)

expect fun KClass<*>.invoke(
    methodName: String,
    arguments: List<Term>,
    instance: Any? = null
): Result

fun Any.assign(propertyName: String, value: Term): Result =
    this::class.assign(propertyName, value, this)

expect fun KClass<*>.assign(
    propertyName: String,
    value: Term,
    instance: Any? = null
): Result

expect fun KClass<*>.create(
    arguments: List<Term>
): Result

internal const val id = "[a-zA-Z_][a-zA-Z0-9_]+"

expect val CLASS_NAME_PATTERN: Regex

expect val KClass<*>.companionObjectRef: Optional<out Any>

expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

expect fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>
