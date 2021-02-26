package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import kotlin.js.JsName

interface State {
    @JsName("context")
    val context: ClassicExecutionContext

    @JsName("next")
    fun next(): State

    @JsName("clone")
    fun clone(context: ClassicExecutionContext = this.context): State
}
