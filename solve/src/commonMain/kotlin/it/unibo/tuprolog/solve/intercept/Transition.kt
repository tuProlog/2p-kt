package it.unibo.tuprolog.solve.intercept

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.js.JsName

/**
 * A port reached by a call, as a [TransitionInterceptor] sees it. Valid only during the callback it is passed to: copy
 * what you need to keep. Solvers may compute [goal] and [context] when they are first read, from their current state,
 * so a transition read after the callback returns may show the state of a later moment.
 */
interface Transition {
    /** The port reached. */
    @JsName("port")
    val port: Port

    /** The goal of the call, with the bindings it has at this port (none, at [Port.CALL], [Port.REDO] and [Port.FAIL]). */
    @JsName("goal")
    val goal: Struct

    /** The id of the call, the same at all of its ports, and unique within the resolution. */
    @JsName("invocation")
    val invocation: Long

    /** The logical call depth of the call: the query's goals are at depth 1. */
    @JsName("depth")
    val depth: Int

    /** A read-only view of the resolution's state at this port. */
    @JsName("context")
    val context: ExecutionContext

    /** The error leaving the call, at [Port.EXCEPTION]; `null` at the other ports. */
    @JsName("error")
    val error: ResolutionException?
}
