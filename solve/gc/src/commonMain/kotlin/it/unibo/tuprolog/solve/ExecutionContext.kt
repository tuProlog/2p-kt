package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.solve.fsm.State
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.utils.Cursor

/** A class representing the Solver execution context, containing important information that determines its behaviour */
data class ExecutionContext(
        val query: Struct,
        val goals: Cursor<out Struct> = Cursor.empty(),
        val rules: Cursor<out Rule> = Cursor.empty(),
        val primitives: Cursor<out Solve.Response> = Cursor.empty(),
        /** Loaded libraries */
        val libraries: Libraries = Libraries(),
        /** Enabled flags */
        val flags: Map<Atom, Term> = emptyMap(),
        /** Static Knowledge-base, that is a KB that *can't* change executing goals */
        val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
        val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        /** When the overall computation started */
        val startTime: Long = 0,
        val maxDuration: Long = Long.MAX_VALUE,
        /** The set of actual substitution till this execution context */
        val substitution: Substitution.Unifier = Substitution.empty(),
        val choicePoints: ChoicePointContext? = null,
        val parent: ExecutionContext? = null,
        val depth: Int = 0,
        val step: Long = 0
) {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    val isActivationRecord: Boolean
        get() = parent == null || parent.depth == depth - 1

    val pathToRoot: Sequence<ExecutionContext> = sequence {
        var current: ExecutionContext? = this@ExecutionContext
        while (current != null) {
            yield(current!!)
            current = current.parent
        }
    }
}