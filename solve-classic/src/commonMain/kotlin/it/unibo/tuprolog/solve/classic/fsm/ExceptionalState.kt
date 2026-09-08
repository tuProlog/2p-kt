package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.js.JsName

/**
 * A [State] reached because of a [ResolutionException] -- either `StateException` (still looking for an
 * enclosing `catch/3`) or `StateHalt` (no handler was found, or the exception reached the root context).
 */
interface ExceptionalState : State {
    /** The exception that led to this state. */
    @JsName("exception")
    val exception: ResolutionException
}
