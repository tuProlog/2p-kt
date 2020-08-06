package it.unibo.tuprolog.solve.libs.oop

import kotlin.reflect.KClass

interface TypeRef : Ref {
    val type: KClass<*>

    companion object {
        fun of(type: KClass<*>): TypeRef = TODO()
    }
}