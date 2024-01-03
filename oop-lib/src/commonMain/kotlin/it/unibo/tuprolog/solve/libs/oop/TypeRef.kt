package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TypeRefImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

interface TypeRef : Ref {
    val type: KClass<*>

    fun create(
        objectConverter: TermToObjectConverter,
        vararg arguments: Term,
    ): Result = create(objectConverter, listOf(*arguments))

    fun create(
        objectConverter: TermToObjectConverter,
        arguments: List<Term>,
    ): Result

    fun create(
        objectConverter: TermToObjectConverter,
        arguments: Iterable<Term>,
    ): Result = create(objectConverter, arguments.toList())

    fun create(
        objectConverter: TermToObjectConverter,
        arguments: Sequence<Term>,
    ): Result = create(objectConverter, arguments.toList())

    fun create(vararg arguments: Term): Result = create(TermToObjectConverter.default, listOf(*arguments))

    fun create(arguments: List<Term>): Result = create(TermToObjectConverter.default, arguments)

    fun create(arguments: Iterable<Term>): Result = create(TermToObjectConverter.default, arguments.toList())

    fun create(arguments: Sequence<Term>): Result = create(TermToObjectConverter.default, arguments.toList())

    companion object {
        @JvmStatic
        fun of(type: KClass<*>): TypeRef = TypeRefImpl(type)
    }
}
