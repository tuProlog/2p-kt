package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.appendRules

internal data class StateRuleSelection(override val context: ExecutionContextImpl) : AbstractState(context) {

    companion object {
        val transparentToCut: Set<Signature> = setOf(
            Signature(",", 2),
            Signature(";", 2),
            Signature("->", 2)
        )
    }

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

    private val ExecutionContextImpl.minDepthToCut: Int
        get() = this.pathToRoot
//            .filter { it.procedure != null }
            .firstOrNull { it.procedure?.extractSignature() !in transparentToCut }
//            .drop(if (goals.current.let { it != null && it.isCut() }) 1 else 0)
//            .filter { it.goals.current !== null }
//            .firstOrNull { it.goals.current!!.extractSignature() !in transparentToCut }
            ?.depth ?: -1

    override fun computeNext(): State {
        return context.goals.current!!.let { currentGoal ->
            with(context) {
                val ruleSources = sequenceOf(libraries.theory, staticKB, dynamicKB)

                when {
                    currentGoal is Truth -> {
                        if (currentGoal.isTrue) ignoreState else failureState
                    }

                    currentGoal.isCut() -> {
                        val depthToCut = this.minDepthToCut

                        if (depthToCut < 0) {
                            ignoreState
                        } else {
                            with(ignoreState) {
                                copy(
                                    context = this.context.copy(
                                        choicePoints = this.context
                                            .choicePoints
                                            ?.pathToRoot
                                            ?.firstOrNull { it.executionContext!!.depth < depthToCut }
                                    )
                                )
                            }
                        }
                    }

                    ruleSources.any { currentGoal in it } -> {
                        val rules = ruleSources
                            .flatMap { it[currentGoal] }
                            .map { it.freshCopy() }
                            .ensureRules()

                        val tempExecutionContext = context.copy(
                            goals = currentGoal.toGoals(),
                            procedure = currentGoal,
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