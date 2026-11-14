package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.utils.Cursor

/**
 * "Primitive Selection": inspects [ConcurrentExecutionContext.currentGoal] and either raises an
 * [it.unibo.tuprolog.solve.exception.error.InstantiationError]/[it.unibo.tuprolog.solve.exception.error.TypeError]
 * (goal is a variable or not callable), invokes the matching
 * [it.unibo.tuprolog.solve.primitive.Primitive] if one is registered for the goal's signature in
 * [it.unibo.tuprolog.solve.library.Runtime], or falls through to [StateRuleSelection] otherwise (the goal is a
 * user-/library-defined predicate).
 *
 * When a primitive is invoked, __every response it lazily produces becomes its own [StatePrimitiveExecution]__:
 * this is one of the two points (the other being [StateRuleSelection]) where a single state can fan out into many
 * concurrently-explored successors, e.g. for a backtracking primitive like `between/3` that can succeed several
 * times.
 */
data class StatePrimitiveSelection(
    override val context: ConcurrentExecutionContext,
) : AbstractState(context) {
    private fun exceptionalState(exception: ResolutionException): Iterable<StateException> =
        listOf(
            StateException(
                exception,
                context.copy(step = nextStep()),
            ),
        )

    override fun computeNext(): Iterable<State> =
        with(context) {
            val goal =
                try {
                    currentGoal!!
                } catch (e: SubstitutionApplicationException) {
                    // goals are substituted lazily: a pattern whose functor is bound to a non-atom is not callable
                    val pattern = e.term as? Pattern ?: throw e
                    val error =
                        TypeError.forGoal(
                            context = context,
                            procedure = context.procedure?.extractSignature() ?: Signature("?-", 1),
                            expectedType = TypeError.Expected.CALLABLE,
                            culprit = pattern.functor[e.substitution],
                        )
                    // the goal raising the error is the pattern as written, which the error state can read
                    return listOf(
                        StateException(error, context.copy(goals = Cursor.of(sequenceOf(pattern)), step = nextStep())),
                    )
                }
            when {
                goal.isVar -> {
                    exceptionalState(
                        InstantiationError.forGoal(
                            context = context,
                            procedure = context.procedure!!.extractSignature(),
                            variable = goal.castToVar(),
                        ),
                    )
                }
                // a call whose functor is still a variable: no primitive, its functor is bound by rule selection
                goal is Pattern -> listOf(StateRuleSelection(context.copy(step = nextStep())))
                goal.isStruct -> {
                    val goalStruct = goal.castToStruct()
                    val signature = goalStruct.extractSignature()

                    if (libraries.hasPrimitive(signature)) {
                        val childContext = createChild()
                        try {
                            val request = childContext.toRequest(goalStruct, signature)
                            val primitive =
                                libraries.primitives[signature]
                                    ?: error("Inconsistent behaviour of Library.contains and Library.get")
                            primitive
                                .solve(request)
                                .map { StatePrimitiveExecution(childContext.copy(primitive = it)) }
                                .asIterable()
                        } catch (exception: ResolutionException) {
                            exceptionalState(exception.updateLastContext(childContext.skipThrow()))
                        }
                    } else {
                        listOf(StateRuleSelection(context.copy(step = nextStep())))
                    }
                }
                else -> {
                    exceptionalState(
                        TypeError.forGoal(
                            context = context,
                            procedure = context.procedure?.extractSignature() ?: Signature("?-", 1),
                            expectedType = TypeError.Expected.CALLABLE,
                            culprit = goal,
                        ),
                    )
                }
            }
        }

    override fun clone(context: ConcurrentExecutionContext): StatePrimitiveSelection = copy(context = context)
}
