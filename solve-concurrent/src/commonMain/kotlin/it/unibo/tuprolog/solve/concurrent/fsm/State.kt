package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
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

    @JsName("next")
    fun next(): Iterable<State>

    @JsName("context")
    val context: ConcurrentExecutionContext

    @JsName("clone")
    fun clone(context: ConcurrentExecutionContext = this.context): State
}
