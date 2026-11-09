package it.unibo.tuprolog.solve.intercept

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.js.JsName

/** What a [TransitionInterceptor] decides at a port, before the resolution goes past it. */
sealed interface Decision {
    /** Go on as usual. Allowed at every port. */
    data object Continue : Decision

    /**
     * Make the call fail instead: at [Port.CALL] without running it, at [Port.EXIT] rejecting this answer, at [Port.REDO]
     * without looking for another one. Allowed at these three ports.
     */
    data object Fail : Decision

    /** Run [goal] in place of the call, as a new call with its own ports. Allowed at [Port.CALL] only. */
    data class Redirect(
        @JsName("goal") val goal: Struct,
    ) : Decision

    /**
     * Run the call again from its [Port.CALL], as a new call with its own ports: the bindings it made are undone and
     * the choice points it left are dropped (the outside world is not restored, and primitives are called anew, not
     * replayed). Allowed at [Port.EXIT], [Port.REDO] and [Port.EXCEPTION], while the call's state is still there; at
     * [Port.FAIL], backtracking has already undone it. Solvers that cannot retry reject it as any decision not allowed.
     */
    data object Retry : Decision

    /**
     * End the resolution with a halt solution carrying [error], or a
     * [it.unibo.tuprolog.solve.exception.HaltException] if `null`. Allowed at every port.
     */
    data class Halt(
        @JsName("error") val error: ResolutionException? = null,
    ) : Decision
}
