package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.unify.Unification
import it.unibo.tuprolog.unify.Unification.Companion.matches
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

interface State {
    val context: ExecutionContext

    fun next(): State
}

sealed class AbstractState(override val context: ExecutionContext) : State {

    protected val executionTime: Long by lazy {
        currentTime()
    }

    protected val nextCache: State by lazy {
        val deltaTime = executionTime - context.startTime
        if (deltaTime <= context.maxDuration) {
            computeNext()
        } else {
            StateHalt(TimeOutException(deltaTime = deltaTime, context =  context), context.copy(step = nextStep()))
        }
    }

    override fun next(): State = nextCache

    protected abstract fun computeNext(): State

    fun currentTime(): Long = currentTimeMillis()

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = (context.depth - 1).let { require(it >= 0); it }
}

data class StateInit(override val context: ExecutionContext) : AbstractState(context) {

    override fun computeNext(): State {
        return StateGoalSelection(
                context.copy(
                        goals = context.query.toGoals(),
                        rules = Cursor.empty(),
                        primitives = Cursor.empty(),
                        startTime = executionTime,
                        substitution = Substitution.empty(),
                        parent = null,
                        choicePoints = null,
                        depth = 0,
                        step = 0
                )
        )
    }
}

data class StateGoalSelection(override val context: ExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {

        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                        Solution.Yes(context.query, context.substitution),
                        context.copy(step = nextStep())
                )
            } else {
                StateGoalSelection(
                        with(context.parent!!) {
                            copy(
                                    choicePoints = context.choicePoints,
                                    flags = context.flags,
                                    dynamicKB = context.dynamicKB,
                                    staticKB = context.staticKB,
                                    substitution = context.substitution,
                                    goals = goals.next, // go on with parent's goals
                                    step = nextStep()
                            )
                        }
                )
            }
        } else {
            StatePrimitiveSelection(
                    context.copy(
                            goals = context.goals.map { it[context.substitution] as Struct },
                            step = nextStep()
                    )
            )
        }
    }
}

data class StatePrimitiveSelection(override val context: ExecutionContext) : AbstractState(context) {

    override fun computeNext(): State {
        return with(context) {
            goals.current!!.let { goal ->
                goal.extractSignature().let { signature ->
                    if (libraries.hasPrimitive(signature)) {

                        val req = toRequest(signature, goal.argsList)
                        val primitive = libraries.primitives[signature] ?: error("Inconsistent behaviour of Library.contains and Library.get")
                        val primitiveExecutions = primitive(req).cursor().also { require(!it.isOver) }


                        val tempExecutionContext = context.copy(
                                goals = sequenceOf(goal).ensureStructs(),
                                parent = context,
                                depth = nextDepth(),
                                step = nextStep()
                        )

                        val newChoicePointContext = context.choicePoints.appendPrimitives(primitiveExecutions.next, tempExecutionContext)

                        StatePrimitiveExecution(
                                tempExecutionContext.copy(
                                        primitives = primitiveExecutions,
                                        choicePoints = newChoicePointContext
                                )
                        )
                    } else {
                        StateRuleSelection(
                                context.copy(step = nextStep())
                        )
                    }
                }
            }
        }
    }

}

data class StatePrimitiveExecution(override val context: ExecutionContext) : AbstractState(context) {

    override fun computeNext(): State {
       return with(context) {
           try {
               when (val sol = primitives.current!!.solution) {
                   is Solution.Yes -> {
                       StateGoalSelection(
                               copy(
                                       goals = goals.next,
                                       primitives = Cursor.empty(),
                                       libraries = primitives.current!!.libraries ?: libraries,
                                       staticKB = primitives.current!!.staticKB ?: staticKB,
                                       dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                                       flags = primitives.current!!.flags ?: flags,
                                       substitution = (substitution + sol.substitution) as Substitution.Unifier,
                                       step = nextStep()
                               )
                       )
                   }
                   is Solution.No -> {
                       StateBacktracking(
                               copy(
                                       primitives = Cursor.empty(),
                                       libraries = primitives.current!!.libraries ?: libraries,
                                       staticKB = primitives.current!!.staticKB ?: staticKB,
                                       dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                                       flags = primitives.current!!.flags ?: flags,
                                       step = nextStep()
                               )
                       )
                   }
                   is Solution.Halt -> StateException(sol.exception.also { it.context = parent }, copy(
                           primitives = Cursor.empty(),
                           libraries = primitives.current!!.libraries ?: libraries,
                           staticKB = primitives.current!!.staticKB ?: staticKB,
                           dynamicKB = primitives.current!!.dynamicKB ?: dynamicKB,
                           flags = primitives.current!!.flags ?: flags,
                           step = nextStep()
                   ))
               }
           }  catch (exception: TuPrologRuntimeException) {
               StateException(exception.also { it.context = parent }, copy(step = nextStep()))
           }
       }
    }

}

data class StateException(override val exception: TuPrologRuntimeException, override val context: ExecutionContext) : ExceptionalState, AbstractState(context) {
    override fun computeNext(): State {
        return when (exception) {
            is PrologError -> {
                val catchGoal = context.goals.current!!

                when {
                    catchGoal.let { it.arity == 3 && it.functor == "catch" } -> {
                        val catcher = catchGoal[1].mguWith(exception.errorStruct)

                        when (catcher) {
                            is Substitution.Unifier -> {
                                val newSubstitution = (context.substitution + catcher) as Substitution.Unifier
                                val subGoals =  catchGoal[2][newSubstitution]  as Struct

                                StateGoalSelection(
                                        context.copy(
                                                goals = subGoals.toGoals(),
                                                rules = Cursor.empty(),
                                                primitives = Cursor.empty(),
                                                substitution = newSubstitution,
                                                step = nextStep()
                                        )
                                )
                            }
                            else -> {
                                StateException(
                                        exception,
                                        context.parent!!.copy(step = nextStep())
                                )
                            }
                        }
                    }
                    context.isRoot -> {
                        StateHalt(exception, context.copy(step = nextStep()))
                    }
                    else -> {
                        StateException(
                                exception,
                                context.parent!!.copy(step = nextStep())
                        )
                    }
                }
            }
            is TimeOutException -> StateHalt(exception, context.copy(step = nextStep()))
            else -> StateHalt(exception, context.copy(step = nextStep()))
        }
    }

}

data class StateRuleSelection(override val context: ExecutionContext) : AbstractState(context) {
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
                                goals = sequenceOf(currentGoal).ensureStructs(),
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

data class StateRuleExecution(override val context: ExecutionContext) : AbstractState(context) {
    private val failureState: StateBacktracking by lazy {
        StateBacktracking(
                context.copy(rules = Cursor.empty(), step = nextStep())
        )
    }

    override fun computeNext(): State {
        return with(context) {
            when (val unifier = goals.current!! mguWith rules.current!!.head) {
                is Substitution.Unifier -> {
                    val newSubstitution = (substitution + unifier) as Substitution.Unifier
                    val subGoals = rules.current!!.body[newSubstitution] as Struct

                    StateGoalSelection(
                            copy(
                                    goals = subGoals.toGoals(),
                                    rules = Cursor.empty(),
                                    substitution = newSubstitution,
                                    step = nextStep()
                            )
                    )
                }
                else -> failureState
            }
        }
    }

}

data class StateBacktracking(override val context: ExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return with(context) {
            if (choicePoints === null || !choicePoints.hasOpenAlternatives) {
                StateEnd(solution = Solution.No(query), context = copy(step = nextStep()))
            } else {
                when (val choicePointContext = choicePoints.pathToRoot.first { it.alternatives.hasNext }) {
                    is ChoicePointContext.Rules -> {
                        val tempContext = choicePointContext.executionContext!!.copy(
                                rules = choicePointContext.alternatives,
                                step = nextStep()
                        )

                        val nextChoicePointContext = choicePointContext.copy(
                                alternatives = choicePointContext.alternatives.next,
                                executionContext = tempContext
                        )

                        val nextContext: ExecutionContext = tempContext.copy(choicePoints = nextChoicePointContext)

                        StateRuleExecution(nextContext)
                    }
                    is ChoicePointContext.Primitives -> {
                        val tempContext = choicePointContext.executionContext!!.copy(
                                primitives = choicePointContext.alternatives,
                                step = nextStep()
                        )

                        val nextChoicePointContext = choicePointContext.copy(
                                alternatives = choicePointContext.alternatives.next,
                                executionContext = tempContext
                        )

                        val nextContext: ExecutionContext = tempContext.copy(choicePoints = nextChoicePointContext)

                        StatePrimitiveExecution(nextContext)
                    }
                }
            }
        }
    }

}

interface EndState : State {

    val solution: Solution

    val hasOpenAlternatives: Boolean
        get() = solution is Solution.Yes && context.hasOpenAlternatives
}

interface ExceptionalState : State {
    val exception: TuPrologRuntimeException
}


sealed class AbstractEndState(override val solution: Solution, override val context: ExecutionContext) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}

data class StateEnd(override val solution: Solution, override val context: ExecutionContext) : AbstractEndState(solution, context) {
    override fun computeNext(): State {
        return if (context.hasOpenAlternatives) {
            StateBacktracking(
                    context.copy(step = nextStep())
            )
        } else {
             super.computeNext()
        }
    }
}

data class StateHalt(override val exception: TuPrologRuntimeException, override val context: ExecutionContext) : ExceptionalState, AbstractEndState(Solution.Halt(context.query, exception), context) {
}