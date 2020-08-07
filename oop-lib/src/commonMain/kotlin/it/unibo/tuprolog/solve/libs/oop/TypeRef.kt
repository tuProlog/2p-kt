package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.TypeRefImpl
import kotlin.reflect.KClass

interface TypeRef : Ref {
    val type: KClass<*>

    companion object {
        fun of(type: KClass<*>): TypeRef = TypeRefImpl(type)
    }
}