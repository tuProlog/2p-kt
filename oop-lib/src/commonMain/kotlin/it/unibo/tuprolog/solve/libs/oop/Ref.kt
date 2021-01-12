package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

interface Ref : Atom {
    fun invoke(objectConverter: TermToObjectConverter, methodName: String, vararg arguments: Term): Result =
        invoke(objectConverter, methodName, listOf(*arguments))

    fun invoke(objectConverter: TermToObjectConverter, methodName: String, arguments: List<Term>): Result

    fun invoke(objectConverter: TermToObjectConverter, methodName: String, arguments: Iterable<Term>): Result =
        invoke(objectConverter, methodName, arguments.toList())

    fun invoke(objectConverter: TermToObjectConverter, methodName: String, arguments: Sequence<Term>): Result =
        invoke(objectConverter, methodName, arguments.toList())

    fun invoke(methodName: String, vararg arguments: Term): Result =
        invoke(TermToObjectConverter.default, methodName, listOf(*arguments))

    fun invoke(methodName: String, arguments: List<Term>): Result =
        invoke(TermToObjectConverter.default, methodName, arguments)

    fun invoke(methodName: String, arguments: Iterable<Term>): Result =
        invoke(TermToObjectConverter.default, methodName, arguments.toList())

    fun invoke(methodName: String, arguments: Sequence<Term>): Result =
        invoke(TermToObjectConverter.default, methodName, arguments.toList())

    fun assign(objectConverter: TermToObjectConverter, propertyName: String, value: Term): Boolean

    fun assign(propertyName: String, value: Term): Boolean =
        assign(TermToObjectConverter.default, propertyName, value)
}
