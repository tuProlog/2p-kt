package it.unibo.tuprolog.core

interface Constant : Term {

    override val isConstant: Boolean get() = true

    val value: Any

    /**
     * Empty companion aimed at letting extentions be injected through extention methods
     */
    companion object
}