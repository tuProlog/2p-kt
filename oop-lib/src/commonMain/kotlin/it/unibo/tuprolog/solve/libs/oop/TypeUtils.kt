package it.unibo.tuprolog.solve.libs.oop

import kotlin.reflect.KClass

object TypeUtils {

    private val KClass<*>.allSupertypes: Sequence<KClass<*>>
        get() = supertypes.asSequence()
            .map { it.classifier }
            .filterIsInstance<KClass<*>>()
            .flatMap { sequenceOf(it) + it.allSupertypes }
            .distinct()

    infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean =
        other.allSupertypes.any { it == this }

    infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean =
        other isSupertypeOf this
}