@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.indexed
import it.unibo.tuprolog.utils.name
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
    objectifier: Any?,
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

context(OOPContext)
internal fun Any.invoke(
    method: String,
    arguments: List<Term>,
): Result = this::class.invoke(method, arguments, this)

private fun KCallable<*>.ensureArgumentsListIsOfSize(actualArguments: List<Term>): List<KClass<*>> {
    return formalParameterTypes.also { formalArgumentsTypes ->
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
}

context(OOPContext)
internal fun KClass<*>.invoke(
    method: String,
    arguments: List<Term>,
    instance: Any?,
): Result {
    val methodRef = OverloadSelector.of(this, objectifier).findMethod(method, arguments)
    return methodRef.callWithPrologArguments(arguments, instance)
}

context(OOPContext)
private fun KCallable<*>.callWithPrologArguments(
    arguments: List<Term>,
    instance: Any? = null,
): Result {
    val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
    val args =
        arguments.mapIndexed { i, it ->
            objectifier.convertInto(formalArgumentsTypes[i], it)
        }.toTypedArray()
    return catchingPlatformSpecificException(instance) {
        val result = invoke(instance, *args)
        Result.Value(result, termificator)
    }
}

context(OOPContext)
internal fun Any.assign(
    property: String,
    value: Term,
): Result = this::class.assign(property, value, this)

context(OOPContext)
internal fun KClass<*>.assign(
    property: String,
    value: Term,
    instance: Any?,
): Result {
    val setterRef = OverloadSelector.of(this, objectifier).findProperty(property, value).setterMethod
    return setterRef.callWithPrologArguments(listOf(value), instance)
}

context(OOPContext)
internal fun KClass<*>.create(
    objectConverter: Objectifier,
    arguments: List<Term>,
): Result {
    val constructorRef = OverloadSelector.of(this, objectConverter).findConstructor(arguments)
    return constructorRef.callWithPrologArguments(arguments)
}
