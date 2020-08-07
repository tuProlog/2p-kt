package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.none()

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.none()