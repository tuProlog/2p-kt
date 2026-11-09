package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.intercept.Port
import it.unibo.tuprolog.solve.intercept.Transition
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor

/**
 * A resolution's [TransitionInterceptor] and the calls it has seen (#1010), shared by both engines of this module.
 *
 * Every frame gets an invocation id, in creation order; a traced call (one with ports, i.e. not a control construct)
 * stays open from its CALL to its FAIL or EXCEPTION. Both engines jump straight to the newest choice point on failure,
 * so the ports of the calls they jump over are derived from the ids: on backtracking to a choice point, every open call
 * younger than the choice point's frame has failed, and the choice point's own call is redone if it had exited.
 * Exceptions leave the open calls younger than the catching `catch/3` the same way.
 *
 * shortcut: nested resolutions (`findall/3`, `bagof/3`, ...) are not intercepted, as their options don't carry the
 * interceptor; pass it through [ExecutionContext] when a debugger needs to step into them.
 */
class Interception internal constructor(
    internal val interceptor: TransitionInterceptor,
) {
    private class Call(
        val goal: Struct,
        val depth: Int,
    ) {
        var exited = false
    }

    private var issued = 0L

    // in id order, i.e. call order
    private val open = LinkedHashMap<Long, Call>()

    /** Whether the engine can retry calls ([Decision.Retry]): only the stateful machine can. */
    internal var retrySupported = false

    /** The call a [Decision.Retry] at an EXCEPTION port is about. */
    internal var retried: Long? = null
        private set

    /** The frame an exception is being propagated from, while the FSM unwinds towards a `catch/3`. */
    internal var unwoundFrom: Long? = null

    internal fun nextId(): Long = ++issued

    private class PortTransition(
        override val port: Port,
        override val invocation: Long,
        override val depth: Int,
        override val error: ResolutionException?,
        context: () -> ExecutionContext,
        goal: () -> Struct,
    ) : Transition {
        override val goal: Struct by lazy(goal)

        override val context: ExecutionContext by lazy(context)
    }

    private fun decide(
        transition: Transition,
        vararg allowed: Port,
    ): Decision {
        val decision = interceptor.before(transition)
        val ok =
            when (decision) {
                is Decision.Continue, is Decision.Halt -> true
                is Decision.Fail -> transition.port in allowed && transition.port != Port.FAIL
                is Decision.Redirect -> transition.port == Port.CALL
                is Decision.Retry ->
                    retrySupported && transition.port in setOf(Port.EXIT, Port.REDO, Port.EXCEPTION)
            }
        require(ok) { "$decision is not allowed at ${transition.port}" }
        return decision
    }

    /** The CALL of [goal], at [depth], whose frame is the [id]-th. */
    internal fun call(
        id: Long,
        goal: Struct,
        depth: Int,
        context: () -> ExecutionContext,
    ): Decision {
        val transition = PortTransition(Port.CALL, id, depth, null, context) { goal }
        val decision = decide(transition, Port.CALL)
        if (decision !is Decision.Redirect && decision !is Decision.Halt) open[id] = Call(goal, depth)
        if (decision == Decision.Continue) interceptor.after(transition)
        return decision
    }

    /** The EXIT of the [id]-th frame's call, whose goal [bound] shows with its bindings: `null` if it isn't traced. */
    internal fun exit(
        id: Long,
        bound: (Struct) -> Struct,
        context: () -> ExecutionContext,
    ): Decision? {
        val call = open[id] ?: return null
        val transition = PortTransition(Port.EXIT, id, call.depth, null, context) { bound(call.goal) }
        val decision = decide(transition, Port.EXIT)
        if (decision == Decision.Continue) {
            call.exited = true
            interceptor.after(transition)
        }
        if (decision == Decision.Retry) closeFrom(id)
        return decision
    }

    /**
     * Backtracking to the choice point of the [choice]-th frame, or past every choice point if `null`: FAIL for the open
     * calls younger than that frame, then REDO for its own call if it had exited. Returns [Decision.Fail] if the REDO was
     * refused (the caller then backtracks further), or [Decision.Halt].
     */
    internal fun backtrack(
        choice: Long?,
        context: () -> ExecutionContext,
    ): Decision {
        val threshold = choice ?: Long.MIN_VALUE
        failYoungerThan(threshold, context)?.let { return it }
        val call = choice?.let { open[it] }
        if (call == null || !call.exited) return Decision.Continue
        val transition = PortTransition(Port.REDO, choice, call.depth, null, context) { call.goal }
        return when (val decision = decide(transition, Port.REDO)) {
            is Decision.Fail -> failYoungerThan(threshold - 1, context) ?: Decision.Fail
            is Decision.Continue ->
                decision.also {
                    call.exited = false
                    interceptor.after(transition)
                }
            is Decision.Retry -> decision.also { closeFrom(choice) }
            else -> decision
        }
    }

    private fun failYoungerThan(
        threshold: Long,
        context: () -> ExecutionContext,
    ): Decision.Halt? {
        for (id in open.keys.filter { it > threshold }.asReversed()) {
            val call = open.remove(id)!!
            val transition = PortTransition(Port.FAIL, id, call.depth, null, context) { call.goal }
            val decision = decide(transition)
            if (decision is Decision.Halt) return decision
            interceptor.after(transition)
        }
        return null
    }

    /**
     * [error] is caught by the `catch/3` whose frame is the [catcher]-th, or by nobody if `null`: EXCEPTION for the calls
     * it leaves, i.e. the open ones from that frame on that hadn't exited (or all the open ones, if uncaught). Returns
     * [Decision.Halt], or [Decision.Retry] for the call [retried] names, or `null` to go on.
     */
    internal fun exception(
        error: ResolutionException,
        catcher: Long?,
        context: () -> ExecutionContext,
    ): Decision? {
        val threshold = catcher ?: Long.MIN_VALUE
        unwoundFrom = null
        retried = null
        for (id in open.keys.filter { it >= threshold }.asReversed()) {
            val call = open[id]!!
            if (call.exited) {
                open.remove(id)
                continue
            }
            val transition = PortTransition(Port.EXCEPTION, id, call.depth, error, context) { call.goal }
            val decision = decide(transition, Port.EXCEPTION)
            if (decision is Decision.Retry) {
                closeFrom(id)
                retried = id
                return decision
            }
            open.remove(id)
            if (decision is Decision.Halt) return decision
            interceptor.after(transition)
        }
        return null
    }

    // the call [id] is retried: it, and the calls it made, are no longer open
    private fun closeFrom(id: Long) {
        open.keys.removeAll { it >= id }
    }
}
