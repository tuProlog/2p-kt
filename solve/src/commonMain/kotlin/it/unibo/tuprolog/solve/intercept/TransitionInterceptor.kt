package it.unibo.tuprolog.solve.intercept

import kotlin.js.JsName

/**
 * Observes, and may redirect, the calls of a resolution (#1010), when passed via
 * [it.unibo.tuprolog.solve.SolveOptions.setInterceptor].
 *
 * [before] is called at each port before the resolution goes past it, i.e. before any irreversible work of the
 * transition; [after] once the transition has happened, unless [before] decided otherwise. A decision not allowed at a
 * port throws an [IllegalArgumentException]. Callbacks run on the resolution's thread and must not advance the same
 * resolution (solvers may throw an [IllegalStateException] if they do). A debugger pauses a resolution by blocking in
 * [before], e.g. running the resolution on a thread of its own: the resolution resumes, once, when [before] returns.
 *
 * Solvers may switch off optimisations that would hide ports (e.g. last-call optimisation, which merges EXITs) while
 * an interceptor is set.
 */
interface TransitionInterceptor {
    /** Called at each port, before the resolution goes past it. */
    @JsName("before")
    fun before(transition: Transition): Decision = Decision.Continue

    /** Called once the resolution has gone past the port of [transition]. */
    @JsName("after")
    fun after(transition: Transition) {}
}
