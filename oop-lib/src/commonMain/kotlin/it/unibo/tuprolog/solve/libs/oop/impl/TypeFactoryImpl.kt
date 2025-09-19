package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.kClassFromName
import kotlin.reflect.KClass

internal class TypeFactoryImpl : TypeFactory {
    override fun typeFromName(typeName: String): KClass<*>? = kClassFromName(typeName).value
}
