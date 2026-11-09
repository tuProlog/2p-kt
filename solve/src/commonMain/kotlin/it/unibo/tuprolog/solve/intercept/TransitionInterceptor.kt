package it.unibo.tuprolog.solve.intercept

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.js.JsName

/**
 * The ports of Byrd's box model (#1010): the logical events of a predicate call, as debuggers and tracers show them.
 *
 * A call has one [CALL], then any number of [EXIT]s (one per answer), each but the first preceded by a [REDO], and ends
 * with one [FAIL] (no more answers) or one [EXCEPTION], unless the resolution stops first. Control constructs (`,`/2,
 * `;`/2, `->`/2, `call/N`, `catch/3`, `throw/1`), `true`, `fail`/`false` and `!` have no ports of their own: the goals
 * inside them have.
 */
enum class Port {
    /** The call is about to start. */
    CALL,

    /** The call has succeeded, with the bindings of [Transition.goal]. */
    EXIT,

    /** The call is about to be retried for another answer, after an [EXIT]. */
    REDO,

    /** The call has no more answers. */
    FAIL,

    /** The call has raised [Transition.error], which leaves it. */
    EXCEPTION,
}

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
