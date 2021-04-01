package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException
import it.unibo.tuprolog.solve.libs.oop.impl.OverloadSelectorImpl
import it.unibo.tuprolog.utils.Optional
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.of(objectInstance ?: companionObjectInstance)

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.of(
        if (objectInstance != null) {
            this
        } else {
            companionObject
        }
    )

actual fun kClassFromName(qualifiedName: String): Optional<out KClass<*>> {
    require(CLASS_NAME_PATTERN.matches(qualifiedName)) {
        "`$qualifiedName` must match ${CLASS_NAME_PATTERN.pattern} while it doesn't"
    }
    val kotlinKlass = KotlinToJavaTypeMap[qualifiedName]
    return if (kotlinKlass != null) {
        Optional.of(kotlinKlass)
    } else try {
        Optional.of(Class.forName(qualifiedName).kotlin)
    } catch (e: ClassNotFoundException) {
        Optional.none()
    }
}

private val classNamePattern = "^$id(\\.$id)*$".toRegex()

actual val CLASS_NAME_PATTERN: Regex
    get() = classNamePattern

actual val Any.identifier: String
    get() = System.identityHashCode(this).toString(16)

internal actual fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T
): T = try {
    action()
} catch (e: IllegalCallableAccessException) {
    throw RuntimePermissionException(this, instance, e)
} catch (e: InvocationTargetException) {
    throw OopRuntimeException(this, instance, e.cause ?: e)
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
    get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map {
        it.type.classifier as? KClass<*> ?: Any::class
    }

actual val KClass<*>.fullName: String
    get() = qualifiedName!!

actual val KClass<*>.name: String
    get() = simpleName!!

actual fun KCallable<*>.pretty(): String =
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

actual fun <T> KCallable<T>.invoke(instance: Any?, vararg args: Any?): T =
    instance?.let { call(it, *args) } ?: call(*args)

actual val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>
    get() = setter

actual fun overloadSelector(type: KClass<*>, termToObjectConverter: TermToObjectConverter): OverloadSelector =
    OverloadSelectorImpl(type, termToObjectConverter)
