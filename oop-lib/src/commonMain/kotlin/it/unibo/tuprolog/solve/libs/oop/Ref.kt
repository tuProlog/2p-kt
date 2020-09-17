package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

interface Ref : Atom {
    fun invoke(methodName: String, vararg arguments: Term): Result =
        invoke(methodName, listOf(*arguments))

    fun invoke(methodName: String, arguments: List<Term>): Result

    fun invoke(methodName: String, arguments: Iterable<Term>): Result =
        invoke(methodName, arguments.toList())

    fun invoke(methodName: String, arguments: Sequence<Term>): Result =
        invoke(methodName, arguments.toList())

    fun assign(propertyName: String, value: Term): Boolean
}
