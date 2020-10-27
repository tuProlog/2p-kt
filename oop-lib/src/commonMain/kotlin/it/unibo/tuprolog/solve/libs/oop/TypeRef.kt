package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TypeRefImpl
import kotlin.reflect.KClass

interface TypeRef : Ref {
    val type: KClass<*>

    fun create(vararg arguments: Term): Result =
        create(listOf(*arguments))

    fun create(arguments: List<Term>): Result

    fun create(arguments: Iterable<Term>): Result =
        create(arguments.toList())

    fun create(arguments: Sequence<Term>): Result =
        create(arguments.toList())

    companion object {
        fun of(type: KClass<*>): TypeRef = TypeRefImpl(type)
    }
}
