package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cursor

/**
 * One node of the `:solve-classic` choice-point queue: a saved point in the proof search that
 * [it.unibo.tuprolog.solve.classic.fsm.StateBacktracking] can resume from, chained to its [parent] to form the
 * full lineage of choice points recorded since the query started (see [pathToRoot]).
 *
 * Crucially, a choice point is *not* a single alternative goal to retry: [alternatives] is a lazy [Cursor] over
 * *all* remaining alternatives (primitive responses, for [Primitives]; candidate clauses, for [Rules]) at that
 * point, and only one is ever consumed per resolution step -- the rest stay in the cursor to be pulled later on
 * backtracking. Every [ChoicePointContext] also captures the [executionContext] active when it was recorded,
 * which is what makes it possible to resume an entirely different branch of the proof tree on [backtrack]: not
 * just "try the next alternative", but "restore the whole saved execution-context lineage, then try the next
 * alternative". [Primitives] and [Rules] are otherwise structurally identical, reflecting how both primitives
 * and rules are modelled uniformly as producers of lazy streams of alternatives.
 *
 * @see ClassicExecutionContext.choicePoints
 * @see appendPrimitives
 * @see appendRules
 */
sealed class ChoicePointContext(
    open val alternatives: Cursor<out Any>,
    open val executionContext: ClassicExecutionContext?,
    open val parent: ChoicePointContext?,
    open val depth: Int = 0,
) {
    // This assertion fails on JS since depth is undefined
//    init {
//        require((depth == 0 && parent == null) || (depth > 0 && parent != null)) {
//            """Violated initial constraint for claass ChoicePointContext: (depth == 0 && parent == null) || (depth > 0 && parent != null)
//                |   depth=$depth
//                |   parent=$parent
//            """.trimMargin()
//        }
//    }

    /** Whether this is the first choice point of a resolution (i.e. it has no [parent]). */
    val isRoot: Boolean
        get() = depth == 0

    /** Whether this choice point, or any of its ancestors along [pathToRoot], still has an alternative to try. */
    val hasOpenAlternatives: Boolean
        get() = pathToRoot.any { it.alternatives.hasNext }

    /** This choice point and all its ancestors, from here up to (and including) the root, in that order. */
    val pathToRoot: Sequence<ChoicePointContext>
        get() =
            sequence {
                var curr: ChoicePointContext? = this@ChoicePointContext
                while (curr != null) {
                    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                    yield(curr!!)
                    curr = curr.parent
                }
            }

    /** Shorthand for [executionContext]'s [ClassicExecutionContext.depth], or `null` if [executionContext] is `null`. */
    val executionContextDepth: Int?
        get() = executionContext?.depth

    /** Shorthand for [executionContext]'s [ClassicExecutionContext.procedure], or `null` if [executionContext] is `null`. */
    val executionContextProcedure: Struct?
        get() = executionContext?.procedure

    override fun toString(): String =
        "$typeName(" +
            "alternatives=$alternatives, " +
            if (executionContext === null) {
                "executionContext=$executionContext, "
            } else {
                "executionContextDepth=$executionContextDepth, "
                "executionContextProcedure=$executionContextProcedure, "
            } +
            "depth=$depth" +
            ")"

    protected abstract val typeName: String

    /**
     * Resumes this choice point: restores its saved [executionContext] (carrying over [context]'s current step
     * counter, flags and knowledge bases), advances [alternatives] to the next one, and returns the resulting
     * [ClassicExecutionContext], ready to be handed to
     * [it.unibo.tuprolog.solve.classic.fsm.StatePrimitiveExecution] or
     * [it.unibo.tuprolog.solve.classic.fsm.StateRuleExecution] depending on the concrete subtype.
     */
    abstract fun backtrack(context: ClassicExecutionContext): ClassicExecutionContext

    /** A choice point recording the remaining [it.unibo.tuprolog.solve.primitive.Solve.Response] alternatives of a primitive call. */
    data class Primitives(
        override val alternatives: Cursor<out Solve.Response>,
        override val executionContext: ClassicExecutionContext?,
        override val parent: ChoicePointContext?,
        override val depth: Int,
    ) : ChoicePointContext(alternatives, executionContext, parent, depth) {
        override fun toString(): String = super.toString()

        override val typeName: String
            get() = "Primitives"

        override fun backtrack(context: ClassicExecutionContext): ClassicExecutionContext {
            val tempContext =
                executionContext!!.copy(
                    primitives = alternatives,
                    step = context.step + 1,
                    startTime = context.startTime,
                    flags = context.flags,
                    dynamicKb = context.dynamicKb,
                    staticKb = context.staticKb,
                    operators = context.operators,
                    inputChannels = context.inputChannels,
                    outputChannels = context.outputChannels,
                    libraries = context.libraries,
                    customData = context.customData.discardEphemeral(),
                )

            val nextChoicePointContext =
                copy(
                    alternatives = alternatives.next,
                    executionContext = tempContext,
                )

            return tempContext.copy(choicePoints = nextChoicePointContext)
        }
    }

    /** A choice point recording the remaining candidate [Rule]s of a clause resolution attempt. */
    data class Rules(
        override val alternatives: Cursor<out Rule>,
        override val executionContext: ClassicExecutionContext?,
        override val parent: ChoicePointContext?,
        override val depth: Int,
    ) : ChoicePointContext(alternatives, executionContext, parent, depth) {
        override fun toString(): String = super.toString()

        override val typeName: String
            get() = "Rules"

        override fun backtrack(context: ClassicExecutionContext): ClassicExecutionContext {
            val tempContext =
                executionContext!!.copy(
                    rules = alternatives,
                    step = context.step + 1,
                    startTime = context.startTime,
                    flags = context.flags,
                    dynamicKb = context.dynamicKb,
                    staticKb = context.staticKb,
                    operators = context.operators,
                    inputChannels = context.inputChannels,
                    outputChannels = context.outputChannels,
                    libraries = context.libraries,
                    customData = context.customData.discardEphemeral(),
                )

            val nextChoicePointContext =
                copy(
                    alternatives = alternatives.next,
                    executionContext = tempContext,
                )

            return tempContext.copy(choicePoints = nextChoicePointContext)
        }
    }
}

/** The [ChoicePointContext.depth] a new choice point chained onto `this` one (or `null`, for the root) would have. */
fun ChoicePointContext?.nextDepth(): Int = if (this == null) 0 else this.depth + 1

/** Chains a new [ChoicePointContext.Primitives] recording [alternatives] onto `this` (`null` meaning "the root"). */
fun ChoicePointContext?.appendPrimitives(
    alternatives: Cursor<out Solve.Response>,
    executionContext: ClassicExecutionContext? = null,
): ChoicePointContext = ChoicePointContext.Primitives(alternatives, executionContext, this, nextDepth())

/** Chains a new [ChoicePointContext.Rules] recording [alternatives] onto `this` (`null` meaning "the root"). */
fun ChoicePointContext?.appendRules(
    alternatives: Cursor<out Rule>,
    executionContext: ClassicExecutionContext? = null,
): ChoicePointContext = ChoicePointContext.Rules(alternatives, executionContext, this, nextDepth())
