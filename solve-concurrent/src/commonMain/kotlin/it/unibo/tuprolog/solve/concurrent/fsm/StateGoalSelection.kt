package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

/**
 * "Goal Selection": the entry point of every resolution step, deciding what to do with
 * [ConcurrentExecutionContext.goals]. If the current context's goals are exhausted, either the whole branch
 * succeeded (root context: moves to a successful [StateEnd]) or control returns to the parent context to resolve
 * its remaining goals; otherwise, the current substitution is applied to the remaining goals and the branch moves
 * on to [StatePrimitiveSelection] to resolve the next one.
 */
data class StateGoalSelection(
    override val context: ConcurrentExecutionContext,
) : AbstractState(context) {
    @Suppress("ktlint:standard:discouraged-comment-location")
    override fun computeNext(): Iterable<State> =
        listOf(
            if (context.goals.isOver) {
                if (context.isRoot) {
                    StateEnd(
                        Solution.yes(context.query, context.substitution),
                        context.copy(step = nextStep()),
                    )
                } else {
                    StateGoalSelection(
                        context.parent!!.let {
                            it.copy(
                                flags = context.flags,
                                dynamicKb = context.dynamicKb,
                                staticKb = context.staticKb,
                                substitution = context.substitution.filter(it.interestingVariables),
                                goals = it.goals.next, // go on with parent's goals
                                procedure = it.procedure,
                                step = nextStep(),
                                startTime = context.startTime,
                                operators = context.operators,
                                inputChannels = context.inputChannels,
                                outputChannels = context.outputChannels,
                                libraries = context.libraries,
                                customData = context.customData,
                            )
                        },
                    )
                }
            } else {
                val goalsWithSubstitution = context.goals.map { it[context.substitution] }
                StatePrimitiveSelection(context.copy(goals = goalsWithSubstitution, step = nextStep()))
            },
        )

    override fun clone(context: ConcurrentExecutionContext): StateGoalSelection = copy(context = context)
}
