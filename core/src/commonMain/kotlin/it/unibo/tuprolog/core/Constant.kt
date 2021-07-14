package it.unibo.tuprolog.core

import kotlin.js.JsName

interface Constant : Term {

    override val isConstant: Boolean get() = true

    @JsName("value")
    val value: Any

    override fun freshCopy(): Constant

    override fun freshCopy(scope: Scope): Constant

    override fun asConstant(): Constant = this

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object
}
