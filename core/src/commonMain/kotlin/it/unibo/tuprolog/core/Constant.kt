package it.unibo.tuprolog.core

interface Constant : Term {
    override val isConstant: Boolean get() = false

    val value: Any
}