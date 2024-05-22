package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.kClassFromName
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

internal expect class TypeFactoryImpl : TypeFactory {
    fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>

    override fun typeFromName(typeName: String): KClass<*>?
}
