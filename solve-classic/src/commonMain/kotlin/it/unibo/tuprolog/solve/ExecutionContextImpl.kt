package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.utils.Cursor

data class ExecutionContextImpl(
        override val libraries: Libraries = Libraries(),
        override val flags: Map<Atom, Term> = emptyMap(),
        override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        override val substitution: Substitution.Unifier = Substitution.empty(),
        val query: Struct,
        val goals: Cursor<out Struct> = Cursor.empty(),
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
            yield(current!!)
            current = current.parent
        }
    }

    override val prologStackTrace: Sequence<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
                .filter { it.goals.hasNext }
                .map { it.goals.current!! }
    }
}