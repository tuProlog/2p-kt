package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.sideffects.SideEffectManager
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.AbstractState
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.getSideEffectManager

/**
 * Base class of states representing the computation end
 *
 * @param solve The [Solve.Response] characterizing this Final State
 *
 * @author Enrico
 */
internal sealed class StateEnd(
    private val sourceContext: ExecutionContext,
    override val solve: Solve.Response,
) : AbstractState(solve),
    FinalState {
    override fun behave(): Sequence<State> = emptySequence()

    override val context: StreamsExecutionContext by lazy {
        StreamsExecutionContext(
            sourceContext,
            newCurrentSubstitution = solve.solution.substitution as? Substitution.Unifier ?: Substitution.empty(),
        )
    }

    /** The *True* state is reached when a successful computational path has ended */
    internal data class True(
        private val sourceContext: ExecutionContext,
        override val solve: Solve.Response,
    ) : StateEnd(sourceContext, solve),
        FinalState {
        init {
            require(solve.solution is Solution.Yes) {
                "True end state can be created only with Solution.Yes. Current: `${solve.solution}`"
            }
        }
    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(
        private val sourceContext: ExecutionContext,
        override val solve: Solve.Response,
    ) : StateEnd(sourceContext, solve),
        FinalState {
        init {
            require(solve.solution is Solution.No) {
                "False end state can be created only with Solution.No. Current: `${solve.solution}`"
            }
        }
    }

    /** The *Halt* state is reached when an [HaltException] is caught, terminating the computation */
    internal data class Halt(
        private val sourceContext: ExecutionContext,
        override val solve: Solve.Response,
    ) : StateEnd(sourceContext, solve),
        FinalState {
        init {
            require(
                solve.solution is Solution.Halt,
            ) { "Halt end state can be created only with Solution.Halt. Current: `${solve.solution}`" }
        }

        /** Shorthand property to access `solve.solution.exception` */
        val exception: ResolutionException by lazy { (solve.solution as Solution.Halt).exception }
    }
}

/** Transition from this intermediate state to [StateEnd.True], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndTrue(
    substitution: Substitution.Unifier = Substitution.empty(),
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect,
) = StateEnd.True(
    context,
    solve.replySuccess(
        substitution,
        sideEffectManager ?: solve.context.getSideEffectManager(),
        *sideEffects,
    ),
)

/** Transition from this intermediate state to [StateEnd.False], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndFalse(
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect,
) = StateEnd.False(
    context,
    solve.replyFail(
        sideEffectManager ?: solve.context.getSideEffectManager(),
        *sideEffects,
    ),
)

/** Transition from this intermediate state to [StateEnd.Halt], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndHalt(
    exception: ResolutionException,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect,
) = StateEnd.Halt(
    context,
    solve.replyException(
        exception,
        sideEffectManager
            ?: exception.context.getSideEffectManager()
            ?: solve.context.getSideEffectManager(),
        *sideEffects,
    ),
)

/** Transition from this intermediate state to the correct [StateEnd] depending on provided [solution] */
internal fun IntermediateState.stateEnd(
    solution: Solution,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect,
): StateEnd =
    solution.whenIs(
        yes = { sol ->
            stateEndTrue(
                sol.substitution.takeUnless { it.isEmpty() } ?: solve.context.substitution,
                sideEffectManager,
                *sideEffects,
            )
        },
        no = { stateEndFalse(sideEffectManager, *sideEffects) },
        halt = { stateEndHalt(it.exception, sideEffectManager, *sideEffects) },
    )

/** Transition from this intermediate state to a [StateEnd] containing provided [response] data */
internal fun IntermediateState.stateEnd(response: Solve.Response) =
    with(response) {
        stateEnd(solution, sideEffectManager, *sideEffects.toTypedArray())
    }
