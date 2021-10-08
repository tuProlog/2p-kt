package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Throw
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

    fun next(): Iterable<State>

    val context: ConcurrentExecutionContext

    fun nextStep(): Long = context.step + 1

    fun nextDepth(): Int = context.depth + 1

    // todo if useless remove
    fun previousDepth(): Int = context.depth - 1

    @JsName("clone")
    fun clone(context: ConcurrentExecutionContext = this.context): State

    fun ConcurrentExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first { it.procedure?.functor != Throw.functor }
}
