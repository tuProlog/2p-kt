package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.js.JsName

/** A [State] carrying a [ResolutionException]: [StateException] (still being handled/propagated) or [StateHalt] (unrecoverable, terminal). */
interface ExceptionalState : State {
    /** The exception this state needs to propagate/handle. */
    @JsName("exception")
    val exception: ResolutionException
}
