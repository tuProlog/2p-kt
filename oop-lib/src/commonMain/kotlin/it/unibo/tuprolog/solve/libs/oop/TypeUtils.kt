@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

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

expect val Any.identifier: String

expect val CLASS_NAME_PATTERN: Regex

expect val KClass<*>.companionObjectRef: Optional<out Any>

expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

expect fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>

internal expect fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T

expect fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>>

expect val KCallable<*>.formalParameterTypes: List<KClass<*>>

expect val KClass<*>.fullName: String

expect val KClass<*>.name: String

expect fun KCallable<*>.pretty(): String

expect fun <T> KCallable<T>.invoke(
    instance: Any?,
    vararg args: Any?,
): T

expect val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>

internal expect fun overloadSelector(
    type: KClass<*>,
    termToObjectConverter: TermToObjectConverter,
): OverloadSelector

val KClass<*>.isPrimitiveType: Boolean get() = this in PRIMITIVE_TYPES

infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean = isSupertypeOf(other, false)

fun KClass<*>.isSupertypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.allSupertypes(strict).any { it == this }

fun KClass<*>.superTypeDistance(other: KClass<*>): Int? =
    other
        .allSupertypes(
            false,
        ).indexed()
        .firstOrNull { (_, it) -> it == this }
        ?.index

infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean = isSubtypeOf(other, false)

fun KClass<*>.isSubtypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.isSupertypeOf(this, strict)

fun KClass<*>.subTypeDistance(other: KClass<*>): Int? = other.superTypeDistance(this)

internal fun Any.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
): Result = this::class.invoke(objectConverter, methodName, arguments, this)

private fun KCallable<*>.ensureArgumentsListIsOfSize(actualArguments: List<Term>): List<KClass<*>> =
    formalParameterTypes.also { formalArgumentsTypes ->
        require(formalParameterTypes.size == actualArguments.size) {
            """
            |
            |Error while invoking $name the expected argument types 
            |   ${formalArgumentsTypes.map { it.name }} 
            |are not as many as the as the actual parameters (${formalArgumentsTypes.size} vs. ${actualArguments.size}):
            |   $actualArguments
            |
            """.trimMargin()
        }
    }

internal fun KClass<*>.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
    instance: Any?,
): Result {
    val methodRef = OverloadSelector.of(this, objectConverter).findMethod(methodName, arguments)
    return methodRef.callWithPrologArguments(objectConverter, arguments, instance)
}

private fun KCallable<*>.callWithPrologArguments(
    converter: TermToObjectConverter,
    arguments: List<Term>,
    instance: Any? = null,
): Result {
    val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
    val args =
        arguments
            .mapIndexed { i, it ->
                converter.convertInto(formalArgumentsTypes[i], it)
            }.toTypedArray()
    return catchingPlatformSpecificException(instance) {
        val result = invoke(instance, *args)
        Result.Value(result)
    }
}

internal fun Any.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
): Result = this::class.assign(objectConverter, propertyName, value, this)

internal fun KClass<*>.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
    instance: Any?,
): Result {
    val setterRef = OverloadSelector.of(this, objectConverter).findProperty(propertyName, value).setterMethod
    return setterRef.callWithPrologArguments(objectConverter, listOf(value), instance)
}

internal fun KClass<*>.create(
    objectConverter: TermToObjectConverter,
    arguments: List<Term>,
): Result {
    val constructorRef = OverloadSelector.of(this, objectConverter).findConstructor(arguments)
    return constructorRef.callWithPrologArguments(objectConverter, arguments)
}
