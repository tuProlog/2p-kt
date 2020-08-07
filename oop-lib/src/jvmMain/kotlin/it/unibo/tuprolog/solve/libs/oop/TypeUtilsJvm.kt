package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

actual val KClass<*>.companionObjectRef: Optional<out Any>
    get() = Optional.of(companionObjectInstance)

actual val KClass<*>.companionObjectType: Optional<out KClass<*>>
    get() = Optional.of(companionObject)