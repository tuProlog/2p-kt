package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.TypeFactoryImpl
import kotlin.reflect.KClass

interface TypeFactory {
    companion object {
        val default: TypeFactory = TypeFactoryImpl()
    }

    fun typeFromName(typeName: String): KClass<*>?
}