package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.ReadOnlyExecutionContext
import it.unibo.tuprolog.solve.prolog.stdlib.primitive.Throw
import it.unibo.tuprolog.utils.cursor

/**
 * Common base of every `:solve-prolog` [State]: wraps [computeNext] with a shared timeout check, so no
 * concrete state has to implement `SolveOptions.timeout` handling itself.
 *
 * [next] first registers one leap on the resolution's [PrologExecutionContext.chronometer] ([isTimeout]), which
 * may or may not read the clock (see `it.unibo.tuprolog.utils.time.ChronometerFactory`); if the budget is known to
 * be exceeded it short-circuits straight to an [EndState] (`StateHalt`) carrying a
 * [TimeOutException], bypassing any pending `catch/3` (resource exhaustion is treated as unrecoverable);
 * otherwise it delegates to [computeNext], the actual per-state transition logic.
 */
abstract class AbstractState(
    override val context: PrologExecutionContext,
) : State {
    /**
     * Whether the timeout has expired, as established by the chronometer upon this state's leap. Evaluated once,
     * by [next]. States at mandatory refresh points override this to force a clock reading, via
     * [it.unibo.tuprolog.utils.time.Chronometer.checkNow].
     */
    protected open val isTimeout: Boolean
        get() = context.chronometer.leap()

    override fun next(): State =
        if (isTimeout) {
            StateHalt(
                TimeOutException(
                    exceededDuration = context.maxDuration,
                    context = context,
                ),
                context.copy(step = nextStep()),
            )
        } else {
            computeNext()
        }

    /** The actual, state-specific transition logic, invoked by [next] once the timeout check has passed. */
    protected abstract fun computeNext(): State

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = context.depth - 1

    /** The halt [decision] of a [it.unibo.tuprolog.solve.intercept.TransitionInterceptor] ends the resolution. */
    protected fun halt(decision: Decision.Halt): State =
        StateHalt(decision.error ?: HaltException(context = context), context.copy(step = nextStep()))

    /**
     * The CALL port of [goal], whose frame will have [invocation] as id: `null` to go on with the call, or the state
     * the interceptor's decision leads to instead. Control constructs have no ports.
     */
    protected fun interceptCall(
        goal: Struct,
        invocation: Long,
    ): State? {
        val interception = context.interception
        if (interception == null || goal.isControlConstruct) return null
        return when (
            val decision =
                interception.call(invocation, goal, context.depth + 1) {
                    ReadOnlyExecutionContext(context)
                }
        ) {
            is Decision.Continue -> null
            is Decision.Fail -> StateBacktracking(context.copy(step = nextStep()))
            is Decision.Redirect ->
                StatePrimitiveSelection(
                    context.copy(
                        goals = (sequenceOf<Term>(decision.goal) + context.goals.next.asSequence()).cursor(),
                        step = nextStep(),
                    ),
                )
            is Decision.Halt -> halt(decision)
        }
    }

    protected fun PrologExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
