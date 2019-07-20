package it.unibo.tuprolog.core

interface Constant : Term {

    override val isConstant: Boolean get() = true

    override fun <T> accept(visitor: TermVisitor<T>): T =
            visitor.visit(this)

    val value: Any
}