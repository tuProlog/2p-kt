package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.utils.Cursor

import kotlin.collections.Set as KtSet

data class ExecutionContextImpl(
    override val procedure: Struct?,
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    val query: Struct,
    val goals: Cursor<out Term> = Cursor.empty(),
    val rules: Cursor<out Rule> = Cursor.empty(),
    val primitives: Cursor<out Solve.Response> = Cursor.empty(),
    val startTime: TimeInstant = 0,
    val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    val choicePoints: ChoicePointContext? = null,
    val parent: ExecutionContextImpl? = null,
    val depth: Int = 0,
    val step: Long = 0
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    val isActivationRecord: Boolean
        get() = parent == null || parent.depth == depth - 1

    val pathToRoot: Sequence<ExecutionContextImpl> = sequence {
        var current: ExecutionContextImpl? = this@ExecutionContextImpl
        while (current != null) {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            yield(current!!)
            current = current.parent
        }
    }

    val currentGoal: Term?
        get() = goals.current

    val interestingVariables: KtSet<Var> by lazy {
        val baseInterestingVars: KtSet<Var> = parent?.interestingVariables ?: query.variables.toSet()
        val currInterestingVars: KtSet<Var> = if (goals.isOver) emptySet() else goals.current?.variables?.toSet() ?: emptySet()

        baseInterestingVars + currInterestingVars
    }

    override val prologStackTrace: Sequence<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
            .filter { it.currentGoal !== null }
            .map { it.currentGoal as Struct }
    }

    override fun toString(): String {
        return "ExecutionContextImpl(" +
                "query=$query, " +
                "procedure=$procedure, " +
                "substitution=$substitution, " +
                "goals=$goals, " +
                "rules=$rules, " +
                "primitives=$primitives, " +
                "startTime=$startTime, " +
                "maxDuration=$maxDuration, " +
                "choicePoints=$choicePoints, " +
                "depth=$depth, " +
                "step=$step" +
                ")"
    }


}