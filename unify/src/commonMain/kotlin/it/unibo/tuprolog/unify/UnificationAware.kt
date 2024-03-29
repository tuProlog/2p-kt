package it.unibo.tuprolog.unify

import kotlin.js.JsName

interface UnificationAware {
    @JsName("unificator")
    val unificator: Unificator
}
