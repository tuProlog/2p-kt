@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter

internal const val id = "[a-zA-Z_][a-zA-Z0-9_]+"

expect val Any.identifier: String

expect val CLASS_NAME_PATTERN: Regex

expect val KClass<*>.companionObjectRef: Optional<out Any>

expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

expect fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>

internal expect fun <T> KCallable<*>.catchingPlatformSpecificException(instance: Any?, action: () -> T): T

fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>> =
    supertypes.asSequence()
        .map { it.classifier }
        .filterIsInstance<KClass<*>>()
        .flatMap { sequenceOf(it) + it.allSupertypes(true) }
        .distinct()
        .let {
            if (strict) it else sequenceOf(this) + it
        }

val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map {
        it.type.classifier as? KClass<*> ?: Any::class
    }

val KClass<*>.fullName: String
    get() = qualifiedName!!

val KClass<*>.name: String
    get() = simpleName!!

infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean =
    isSupertypeOf(other, false)

fun KClass<*>.isSupertypeOf(other: KClass<*>, strict: Boolean): Boolean =
    other.allSupertypes(strict).any { it == this }

infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean =
    isSubtypeOf(other, false)

fun KClass<*>.isSubtypeOf(other: KClass<*>, strict: Boolean): Boolean =
    other.isSupertypeOf(this, strict)

fun Any.invoke(objectConverter: TermToObjectConverter, methodName: String, arguments: List<Term>): Result =
    this::class.invoke(objectConverter, methodName, arguments, this)

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

fun KClass<*>.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
    instance: Any?
): Result {
    val methodRef = OverloadSelector.of(this, objectConverter).findMethod(methodName, arguments)
    return methodRef.callWithPrologArguments(objectConverter, arguments, instance)
}

private fun KCallable<*>.callWithPrologArguments(
    converter: TermToObjectConverter,
    arguments: List<Term>,
    instance: Any? = null
): Result {
    val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
    val args = arguments.mapIndexed { i, it ->
        converter.convertInto(formalArgumentsTypes[i], it)
    }.toTypedArray()
    return catchingPlatformSpecificException(instance) {
        val result = if (instance == null) call(*args) else call(instance, *args)
        Result.Value(result)
    }
}

fun Any.assign(objectConverter: TermToObjectConverter, propertyName: String, value: Term): Result =
    this::class.assign(objectConverter, propertyName, value, this)

fun KClass<*>.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
    instance: Any?
): Result {
    val setterRef = OverloadSelector.of(this, objectConverter).findProperty(propertyName, value).setter
    return setterRef.callWithPrologArguments(objectConverter, listOf(value), instance)
}

fun KClass<*>.create(
    objectConverter: TermToObjectConverter,
    arguments: List<Term>
): Result {
    val constructorRef = OverloadSelector.of(this, objectConverter).findConstructor(arguments)
    return constructorRef.callWithPrologArguments(objectConverter, arguments)
}

fun KCallable<*>.pretty(): String =
    "$name(${parameters.map { it.pretty() }}): ${returnType.classifier.pretty()}"

private fun KClassifier?.pretty(): String =
    if (this is KClass<*>) {
        fullName
    } else {
        "$this"
    }

private fun KParameter.pretty(): String =
    when (kind) {
        KParameter.Kind.INSTANCE -> "<this>"
        KParameter.Kind.EXTENSION_RECEIVER -> "<this>:${type.classifier.pretty()}"
        else -> "$name:${type.classifier}"
    }
