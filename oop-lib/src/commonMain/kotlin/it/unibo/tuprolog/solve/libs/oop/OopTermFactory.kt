package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.TermFactory
import kotlin.reflect.KClass

interface OopTermFactory : TermFactory {
    val typeFactory: TypeFactory

    fun typeRef(name: String): TypeRef

    fun typeRef(value: KClass<*>): TypeRef

    companion object {
        fun of(typeFactory: TypeFactory): OopTermFactory = TODO()
        val default: OopTermFactory = of(TypeFactory.default)
    }
}
