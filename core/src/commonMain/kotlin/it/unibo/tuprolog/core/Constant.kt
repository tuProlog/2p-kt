package it.unibo.tuprolog.core

interface Constant : Term {

    override val isConstant: Boolean get() = true

    val value: Any
}