@file:Suppress("TooManyFunctions")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException
import it.unibo.tuprolog.solve.libs.oop.impl.OverloadSelectorImpl
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.fullName
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.KProperty2
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.of(objectInstance ?: companionObjectInstance)

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() =
        Optional.of(
            if (objectInstance != null) {
                this
            } else {
                companionObject
            },
        )

private val classNamePattern = "^$ID(\\.$ID(\\$$ID)*)*$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

internal actual fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T =
    try {
        action()
    } catch (e: IllegalCallableAccessException) {
        throw RuntimePermissionException(this, instance, e)
    } catch (e: InvocationTargetException) {
        throw OopRuntimeException(this, instance, e.targetException ?: e.cause ?: e)
    } catch (e: IllegalArgumentException) {
        throw OopRuntimeException(this, instance, e.cause ?: e)
    }

actual fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>> =
    supertypes.asSequence()
        .map { it.classifier }
        .filterIsInstance<KClass<*>>()
        .flatMap { sequenceOf(it) + it.allSupertypes(true) }
        .distinct()
        .let { if (strict) it else sequenceOf(this) + it }

actual val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() =
        parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map {
            it.type.classifier as? KClass<*> ?: Any::class
        }

actual fun KCallable<*>.pretty(): String = "$name(${parameters.map { it.pretty() }}): ${returnType.classifier.pretty()}"

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

@Suppress("UNCHECKED_CAST")
actual fun <T> KCallable<T>.invoke(
    objectifier: Any?,
    vararg method: Any?,
): T =
    when (this) {
        is KProperty0<T> -> get()
        is KProperty1<*, T> -> {
            val property = this as KProperty1<Any?, T>
            property.get(objectifier)
        }
        is KProperty2<*, *, T> -> {
            val property = this as KProperty2<Any?, Any?, T>
            property.get(objectifier, method[0])
        }
        else -> {
            if (objectifier == null) {
                call(*method)
            } else {
                call(objectifier, *method)
            }
        }
    }

actual val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>
    get() = setter

actual fun overloadSelector(
    type: KClass<*>,
    objectifier: Objectifier,
): OverloadSelector = OverloadSelectorImpl(type, objectifier)
