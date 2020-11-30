package it.unibo.tuprolog.solve.problogclassic

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cursor

sealed class ProblogChoicePointContext(
    open val alternatives: Cursor<out Any>,
    open val executionContext: ProblogClassicExecutionContext?,
    open val parent: ProblogChoicePointContext?,
    open val depth: Int = 0
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

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = pathToRoot.any { it.alternatives.hasNext }

    val pathToRoot: Sequence<ProblogChoicePointContext>
        get() = sequence {
            var curr: ProblogChoicePointContext? = this@ProblogChoicePointContext
            while (curr != null) {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                yield(curr!!)
                curr = curr.parent
            }
        }

    val executionContextDepth: Int?
        get() = executionContext?.depth

    val executionContextProcedure: Struct?
        get() = executionContext?.procedure

    override fun toString(): String = "$typeName(" +
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

    abstract fun backtrack(context: ProblogClassicExecutionContext): ProblogClassicExecutionContext

    data class Primitives(
        override val alternatives: Cursor<out Solve.Response>,
        override val executionContext: ProblogClassicExecutionContext?,
        override val parent: ProblogChoicePointContext?,
        override val depth: Int
    ) : ProblogChoicePointContext(alternatives, executionContext, parent, depth) {

        override fun toString(): String {
            return super.toString()
        }

        override val typeName: String
            get() = "Primitives"

        override fun backtrack(context: ProblogClassicExecutionContext): ProblogClassicExecutionContext {
            val tempContext = executionContext!!.copy(
                primitives = alternatives,
                step = context.step + 1,
                startTime = context.startTime,
                flags = context.flags,
                dynamicKb = context.dynamicKb,
                staticKb = context.staticKb,
                operators = context.operators,
                inputChannels = context.inputChannels,
                outputChannels = context.outputChannels,
                libraries = context.libraries
            )

            val nextChoicePointContext = copy(
                alternatives = alternatives.next,
                executionContext = tempContext
            )

            return tempContext.copy(choicePoints = nextChoicePointContext)
        }
    }

    data class Rules(
        override val alternatives: Cursor<out Rule>,
        override val executionContext: ProblogClassicExecutionContext?,
        override val parent: ProblogChoicePointContext?,
        override val depth: Int
    ) : ProblogChoicePointContext(alternatives, executionContext, parent, depth) {

        override fun toString(): String {
            return super.toString()
        }

        override val typeName: String
            get() = "Rules"

        override fun backtrack(context: ProblogClassicExecutionContext): ProblogClassicExecutionContext {
            val tempContext = executionContext!!.copy(
                rules = alternatives,
                step = context.step + 1,
                startTime = context.startTime,
                flags = context.flags,
                dynamicKb = context.dynamicKb,
                staticKb = context.staticKb,
                operators = context.operators,
                inputChannels = context.inputChannels,
                outputChannels = context.outputChannels,
                libraries = context.libraries
            )

            val nextChoicePointContext = copy(
                alternatives = alternatives.next,
                executionContext = tempContext
            )

            return tempContext.copy(choicePoints = nextChoicePointContext)
        }
    }
}

internal fun ProblogChoicePointContext?.nextDepth(): Int = if (this == null) 0 else this.depth + 1

internal fun ProblogChoicePointContext?.appendPrimitives(
    alternatives: Cursor<out Solve.Response>,
    executionContext: ProblogClassicExecutionContext? = null
): ProblogChoicePointContext = ProblogChoicePointContext.Primitives(alternatives, executionContext, this, nextDepth())

internal fun ProblogChoicePointContext?.appendRules(
    alternatives: Cursor<out Rule>,
    executionContext: ProblogClassicExecutionContext? = null
): ProblogChoicePointContext = ProblogChoicePointContext.Rules(alternatives, executionContext, this, nextDepth())
