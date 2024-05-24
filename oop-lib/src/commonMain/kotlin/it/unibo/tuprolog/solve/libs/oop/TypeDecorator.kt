package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term

interface TypeDecorator : OOPContext, TypeRef {
    fun create(vararg arguments: Term): Result

    fun create(arguments: List<Term>): Result

    fun create(arguments: Iterable<Term>): Result

    fun create(arguments: Sequence<Term>): Result
}