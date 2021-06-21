package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import kotlin.js.JsName

interface State {

    @JsName("isEndState")
    val isEndState: Boolean
        get() = false

    @JsName("asEndState")
    fun asEndState(): EndState? = null

    @JsName("castToEndState")
    fun castToEndState(): EndState =
        asEndState() ?: throw ClassCastException("Cannot cast $this to ${EndState::class.simpleName}")

    @JsName("context")
    val context: ClassicExecutionContext

    @JsName("next")
    fun next(): State

    @JsName("clone")
    fun clone(context: ClassicExecutionContext = this.context): State
}
