package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.library.stdlib.magic.MagicCut
import it.unibo.tuprolog.solve.primitive.Signature
import it.unibo.tuprolog.solve.primitive.extractSignature
import it.unibo.tuprolog.solve.ChoicePointContext
import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal data class StateRuleSelection(override val context: ClassicExecutionContext) : AbstractState(context) {

    companion object {
        val transparentToCut = setOf(
            Signature(",", 2),
            Signature(";", 2),
            Signature("->", 2)
        )

        private sealed class CutLimit {
            abstract val depthToCut: Int
            abstract val procedure: Struct?

            object None : CutLimit() {
                override val depthToCut: Int = -1
                override val procedure: Struct? = null
            }

            class Actual(override val depthToCut: Int, override val procedure: Struct?) : CutLimit()
        }
    }

    private val failureState: StateBacktracking
        get() = StateBacktracking(
            context.copy(step = nextStep())
        )

    private fun exceptionalState(exception: TuPrologRuntimeException): StateException =
        StateException(
            exception,
            context.copy(step = nextStep())
        )

    private val ignoreState: StateGoalSelection
        get() = StateGoalSelection(
            context.copy(goals = context.goals.next, step = nextStep())
        )

    private fun Term.isCut(): Boolean = this is Atom && value == "!"

    private fun ClassicExecutionContext.computeCutLimit(magicCut: Boolean = false): CutLimit {
            val cutLimit = if (magicCut) {
                this.pathToRoot.firstOrNull()
            } else {
                this.pathToRoot.firstOrNull { it.procedure?.extractSignature() !in transparentToCut }
            }
            return if (cutLimit == null) {
                CutLimit.None
            } else {
                CutLimit.Actual(cutLimit.depth, cutLimit.procedure)
            }
        }

    private fun ChoicePointContext?.performCut(cutLimit: CutLimit): ChoicePointContext? {
        return when {
            this === null -> {
                null
            }
            cutLimit is CutLimit.None ||
            cutLimit.depthToCut > executionContext!!.depth ||
            cutLimit.depthToCut == executionContext!!.depth && cutLimit.procedure != executionContext!!.procedure -> {
                this
            }
            else -> {
                val cutCandidates = pathToRoot.filter {
                    it.executionContext!!.procedure == cutLimit.procedure
                }

                if (cutCandidates.any()) {
                    cutCandidates.firstOrNull {
                        it.executionContext!!.depth <= cutLimit.depthToCut
                    }?.parent
                } else {
                    this
                }
            }
        }
    }

    override fun computeNext(): State {

        return when (val currentGoal = context.currentGoal!!) {
            is Var -> {
                exceptionalState(
                    InstantiationError.forGoal(
                        context = context,
                        procedure = context.procedure!!.extractSignature(),
                        variable = currentGoal
                    )
                )
            }
            is Struct -> with(context) {
                val ruleSources = sequenceOf(libraries.theory, staticKB, dynamicKB)

                when {
                    currentGoal is Truth -> {
                        if (currentGoal.isTrue) ignoreState else failureState
                    }

                    currentGoal.isCut() -> {
                        val cutLimit = computeCutLimit(currentGoal is MagicCut)

                        ignoreState.copy(
                            context = ignoreState.context.copy(
                                choicePoints = ignoreState.context.choicePoints.performCut(cutLimit)
                            )
                        )
                    }

                    ruleSources.any { currentGoal in it } -> {
                        val rules = ruleSources
                            .flatMap { it[currentGoal] }
                            .map { it.freshCopy() }
                            .ensureRules()

                        StateRuleExecution(
                            context.createChildAppendingRulesAndChoicePoints(rules)
                        )
                    }

                    else -> failureState
                }
            }
            else -> {
                exceptionalState(
                    TypeError.forGoal(
                        context = context,
                        procedure = context.procedure!!.extractSignature(),
                        expectedType = TypeError.Expected.CALLABLE,
                        actualValue = currentGoal
                    )
                )
            }
        }
    }
}