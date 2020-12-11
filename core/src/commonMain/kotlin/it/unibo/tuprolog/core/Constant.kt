package it.unibo.tuprolog.core

import kotlin.js.JsName

interface Constant : Term {

    override val isConstant: Boolean get() = true

    @JsName("value")
    val value: Any

    override fun tag(name: String, value: Any): Constant

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object
}
