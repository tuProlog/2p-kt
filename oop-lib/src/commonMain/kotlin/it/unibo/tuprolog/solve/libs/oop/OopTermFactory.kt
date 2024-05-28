package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.TermFactory
import it.unibo.tuprolog.solve.libs.oop.impl.OopTermFactoryImpl
import kotlin.reflect.KClass

interface OopTermFactory : TermFactory {
    val typeFactory: TypeFactory

    fun typeRef(name: String): TypeRef

    fun typeRef(value: KClass<*>): TypeRef

    companion object {
        fun of(
            typeFactory: TypeFactory,
            termFactory: TermFactory = TermFactory.default,
        ): OopTermFactory = OopTermFactoryImpl(typeFactory, termFactory)

        val default: OopTermFactory = of(TypeFactory.default)
    }
}
