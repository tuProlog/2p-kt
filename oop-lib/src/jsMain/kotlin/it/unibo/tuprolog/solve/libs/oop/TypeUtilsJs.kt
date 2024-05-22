package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty

private val TODO_EXCEPTION = NotImplementedError("OOP-Prolog integration is still not supported on JS")

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.none()

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.none()

internal actual fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T = action()

actual fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>> = throw TODO_EXCEPTION

actual val KCallable<*>.formalParameterTypes: List<KClass<*>>
    get() = throw TODO_EXCEPTION

actual fun KCallable<*>.pretty(): String = throw TODO_EXCEPTION

actual fun <T> KCallable<T>.invoke(
    instance: Any?,
    vararg args: Any?,
): T = throw TODO_EXCEPTION

actual val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>
    get() = throw TODO_EXCEPTION

actual fun overloadSelector(
    type: KClass<*>,
    objectifier: Objectifier,
): OverloadSelector = throw TODO_EXCEPTION
