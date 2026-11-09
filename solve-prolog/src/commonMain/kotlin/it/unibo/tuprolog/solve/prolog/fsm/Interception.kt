package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.intercept.Port
import it.unibo.tuprolog.solve.intercept.Transition
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.ReadOnlyExecutionContext

/**
 * A resolution's [TransitionInterceptor] and the calls it has seen (#1010), shared by every frame of the resolution.
 *
 * Every frame gets a [PrologExecutionContext.invocation] id, in creation order; a traced call (one with ports, i.e. not
 * a control construct) stays open from its CALL to its FAIL or EXCEPTION. This FSM jumps straight to the newest choice
 * point on failure, so the ports of the calls it jumps over are derived from the ids: on backtracking to a choice point,
 * every open call younger than the choice point's frame has failed, and the choice point's own call is redone if it
 * had exited. Exceptions leave the open calls younger than the catching `catch/3` the same way.
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

    // the frame an exception is being propagated from, while unwinding towards a catch/3
    private var unwoundFrom: Long? = null

    internal fun nextId(): Long = ++issued

    private inner class PortTransition(
        override val port: Port,
        override val invocation: Long,
        override val depth: Int,
        private val frame: PrologExecutionContext,
        override val error: ResolutionException?,
        goal: () -> Struct,
    ) : Transition {
        override val goal: Struct by lazy(goal)

        override val context: ExecutionContext by lazy { ReadOnlyExecutionContext(frame) }
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
            }
        require(ok) { "$decision is not allowed at ${transition.port}" }
        return decision
    }

    /** The CALL of [goal], whose frame will be the [id]-th, a child of [frame]. */
    internal fun call(
        id: Long,
        goal: Struct,
        frame: PrologExecutionContext,
    ): Decision {
        val transition = PortTransition(Port.CALL, id, frame.depth + 1, frame, null) { goal }
        val decision = decide(transition, Port.CALL)
        if (decision !is Decision.Redirect && decision !is Decision.Halt) open[id] = Call(goal, frame.depth + 1)
        if (decision == Decision.Continue) interceptor.after(transition)
        return decision
    }

    /** The EXIT of [frame]'s call, if traced: `null` if it isn't. */
    internal fun exit(frame: PrologExecutionContext): Decision? {
        val call = open[frame.invocation] ?: return null
        val transition =
            PortTransition(Port.EXIT, frame.invocation, call.depth, frame, null) {
                call.goal[frame.substitution].castToStruct()
            }
        val decision = decide(transition, Port.EXIT)
        if (decision == Decision.Continue) {
            call.exited = true
            interceptor.after(transition)
        }
        return decision
    }

    /**
     * Backtracking to the choice point recorded in [choiceFrame], or past every choice point if `null`: FAIL for the
     * open calls younger than [choiceFrame], then REDO for its own call if it had exited. Returns [Decision.Fail] if
     * the REDO was refused (the caller then backtracks further), or [Decision.Halt].
     */
    internal fun backtrack(
        choiceFrame: PrologExecutionContext?,
        frame: PrologExecutionContext,
    ): Decision {
        val threshold = choiceFrame?.invocation ?: Long.MIN_VALUE
        failYoungerThan(threshold, frame)?.let { return it }
        val call = choiceFrame?.let { open[it.invocation] }
        if (call == null || !call.exited) return Decision.Continue
        val transition = PortTransition(Port.REDO, choiceFrame.invocation, call.depth, choiceFrame, null) { call.goal }
        return when (val decision = decide(transition, Port.REDO)) {
            is Decision.Fail -> failYoungerThan(threshold - 1, frame) ?: Decision.Fail
            is Decision.Continue ->
                decision.also {
                    call.exited = false
                    interceptor.after(transition)
                }
            else -> decision
        }
    }

    private fun failYoungerThan(
        threshold: Long,
        frame: PrologExecutionContext,
    ): Decision.Halt? {
        for (id in open.keys.filter { it > threshold }.asReversed()) {
            val call = open.remove(id)!!
            val transition = PortTransition(Port.FAIL, id, call.depth, frame, null) { call.goal }
            val decision = decide(transition)
            if (decision is Decision.Halt) return decision
            interceptor.after(transition)
        }
        return null
    }

    /** An exception propagates from [frame] to its parent. */
    internal fun unwind(frame: PrologExecutionContext) {
        unwoundFrom = frame.invocation
    }

    /**
     * [error] is caught by the `catch/3` of [frame], or by nobody if [caught] is `false`: EXCEPTION for the calls it
     * leaves, i.e. the open ones younger than the `catch/3` that hadn't exited (or all the open ones, if uncaught).
     */
    internal fun exception(
        error: ResolutionException,
        frame: PrologExecutionContext,
        caught: Boolean,
    ): Decision.Halt? {
        val threshold = if (caught) unwoundFrom ?: Long.MAX_VALUE else Long.MIN_VALUE
        unwoundFrom = null
        for (id in open.keys.filter { it >= threshold }.asReversed()) {
            val call = open.remove(id)!!
            if (call.exited) continue
            val transition = PortTransition(Port.EXCEPTION, id, call.depth, frame, error) { call.goal }
            val decision = decide(transition)
            if (decision is Decision.Halt) return decision
            interceptor.after(transition)
        }
        return null
    }
}
