package it.unibo.tuprolog.core

interface Constant : Term {

    override val isConstant: Boolean get() = true

    val value: Any

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object
}