package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.flags.TrackVariables

/**
 * "Goal Selection", the entry point of every resolution step: decides what to do with
 * [ClassicExecutionContext.goals]. Three cases: no goals left and no parent context -> emit the current
 * substitution as a solution and move to `StateEnd`; no goals left but a parent exists -> pop the stack, carrying
 * the child's substitution (filtered down to variables the parent still cares about, via
 * [ClassicExecutionContext.isVariableInteresting]) into the parent, and loop back into `StateGoalSelection` for
 * the parent's remaining goals; goals remain -> move to `StatePrimitiveSelection` (after recording the current
 * goal's variables as relevant, if `TrackVariables` is `ON`).
 */
data class StateGoalSelection(
    override val context: ClassicExecutionContext,
) : AbstractState(context) {
    @Suppress("ktlint:standard:discouraged-comment-location")
    override fun computeNext(): State =
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
                    relevantVariables =
                        context.relevantVariables +
                            context.goals.current!!
                                .variables
                                .toSet(),
                ),
            )
        } else {
            StatePrimitiveSelection(
                context.copy(step = nextStep()),
            )
        }

    override fun clone(context: ClassicExecutionContext): StateGoalSelection = copy(context = context)
}
