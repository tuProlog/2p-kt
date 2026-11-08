package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.ReadOnlyExecutionContext
import it.unibo.tuprolog.utils.cursor

/**
 * "Goal Selection", the entry point of every resolution step: decides what to do with
 * [PrologExecutionContext.goals]. Three cases: no goals left and no parent context -> emit the current
 * substitution as a solution and move to `StateEnd`; no goals left but a parent exists -> pop the stack, carrying
 * the child's substitution (filtered down to variables the parent still cares about, via
 * [PrologExecutionContext.isVariableInteresting]) into the parent, and loop back into `StateGoalSelection` for
 * the parent's remaining goals; goals remain -> move to `StatePrimitiveSelection` (after recording the current
 * goal's variables as relevant, if `TrackVariables` is `ON`).
 */
data class StateGoalSelection(
    override val context: PrologExecutionContext,
) : AbstractState(context) {
    /** A mandatory refresh point before publishing a solution, so that none is published past the deadline. */
    override val isTimeout: Boolean
        get() = if (context.goals.isOver && context.isRoot) context.chronometer.checkNow() else super.isTimeout

    @Suppress("ktlint:standard:discouraged-comment-location")
    override fun computeNext(): State =
        if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.yes(context.query, context.substitution),
                    context.copy(step = nextStep()),
                )
            } else {
                when (val decision = context.interception?.exit(context)) {
                    is Decision.Fail -> return StateBacktracking(context.copy(step = nextStep()))
                    is Decision.Halt -> return halt(decision)
                    else -> Unit
                }
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
                            chronometer = context.chronometer,
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
            val selected = context.selectGoal()
            if (selected.flags[TrackVariables] == TrackVariables.ON) {
                StatePrimitiveSelection(
                    selected.copy(
                        step = nextStep(),
                        relevantVariables =
                            selected.relevantVariables +
                                selected.goals.current!!
                                    .variables
                                    .toSet(),
                    ),
                )
            } else {
                StatePrimitiveSelection(
                    selected.copy(step = nextStep()),
                )
            }
        }

    // moves the goal the policy chooses, if any, to the front of the conjunction
    private fun PrologExecutionContext.selectGoal(): PrologExecutionContext {
        val policy = goalSelection
        if (policy == null || !goals.hasNext) return this
        val remaining = goals.asSequence().toList()
        val index = policy.select(remaining.map { it[substitution] }, ReadOnlyExecutionContext(this))
        require(index in remaining.indices) { "Goal index $index out of ${remaining.indices}" }
        if (index == 0) return this
        val reordered = listOf(remaining[index]) + remaining.filterIndexed { i, _ -> i != index }
        return copy(goals = reordered.cursor())
    }

    override fun clone(context: PrologExecutionContext): StateGoalSelection = copy(context = context)
}
