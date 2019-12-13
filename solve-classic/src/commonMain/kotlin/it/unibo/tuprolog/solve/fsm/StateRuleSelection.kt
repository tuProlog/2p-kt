package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.appendRules

internal data class StateRuleSelection(override val context: ExecutionContextImpl) : AbstractState(context) {
    private val failureState: StateBacktracking by lazy {
        StateBacktracking(
            context.copy(step = nextStep())
        )
    }

    private val ignoreState: StateGoalSelection by lazy {
        StateGoalSelection(
            context.copy(goals = context.goals.next, step = nextStep())
        )
    }

    private fun Term.isCut(): Boolean = this is Atom && value == "!"

    override fun computeNext(): State {
        return context.goals.current!!.let { currentGoal ->
            with(context) {
                val ruleSources = sequenceOf(libraries.theory, staticKB, dynamicKB)

                when {
                    currentGoal is Truth -> {
                        if (currentGoal.isTrue) ignoreState else failureState
                    }
                    currentGoal.isCut() -> {
                        with(ignoreState) {
                            copy(
                                context = this.context.copy(
                                    choicePoints = this.context
                                        .choicePoints
                                        ?.pathToRoot
                                        ?.firstOrNull { it.executionContext!!.depth < depth }
                                )
                            )
                        }
                    }
                    ruleSources.any { currentGoal in it } -> {
                        val rules = ruleSources
                            .flatMap { it[currentGoal] }
                            .map { it.freshCopy() }
                            .ensureRules()

                        val tempExecutionContext = context.copy(
                            goals = currentGoal.toGoals(),
                            parent = context,
                            depth = nextDepth(),
                            step = nextStep()
                        )

                        val newChoicePointContext = context.choicePoints.appendRules(rules.next, tempExecutionContext)

                        StateRuleExecution(
                            tempExecutionContext.copy(
                                rules = rules,
                                choicePoints = newChoicePointContext
                            )
                        )
                    }
                    else -> failureState
                }
            }
        }
    }

}