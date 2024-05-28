package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.DefaultTypeFactory
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

fun interface TypeFactory {
    companion object {
        @JvmStatic
        val default: TypeFactory = DefaultTypeFactory()
    }

    fun typeFromName(typeName: String): KClass<*>?
}
