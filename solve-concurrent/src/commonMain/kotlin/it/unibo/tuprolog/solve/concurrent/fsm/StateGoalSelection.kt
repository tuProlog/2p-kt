package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

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
