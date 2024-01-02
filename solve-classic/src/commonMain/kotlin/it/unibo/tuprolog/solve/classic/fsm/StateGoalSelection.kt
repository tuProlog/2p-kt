package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.flags.TrackVariables

data class StateGoalSelection(override val context: ClassicExecutionContext) : AbstractState(context) {
    @Suppress("ktlint:standard:discouraged-comment-location")
    override fun computeNext(): State {
        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.yes(context.query, context.substitution),
                    context.copy(step = nextStep()),
                )
            } else {
                StateGoalSelection(
                    context.parent!!.let {
                        it.copy(
                            choicePoints = context.choicePoints,
                            flags = context.flags,
                            dynamicKb = context.dynamicKb,
                            staticKb = context.staticKb,
                            substitution = context.substitution.filter { (it, _) -> context.isVariableInteresting(it) },
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
        } else if (context.flags[TrackVariables] == TrackVariables.ON) {
            StatePrimitiveSelection(
                context.copy(
                    step = nextStep(),
                    relevantVariables = context.relevantVariables + context.goals.current!!.variables.toSet(),
                ),
            )
        } else {
            StatePrimitiveSelection(
                context.copy(step = nextStep()),
            )
        }
    }

    override fun clone(context: ClassicExecutionContext): StateGoalSelection = copy(context = context)
}
