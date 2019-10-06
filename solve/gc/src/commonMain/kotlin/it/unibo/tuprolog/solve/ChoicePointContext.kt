package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.utils.Cursor

sealed class ChoicePointContext(
        open val alternatives: Cursor<out Any>,
        open val executionContext: ExecutionContext?,
        open val parent: ChoicePointContext?,
        open val depth: Int = 0
) {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = pathToRoot.any { it.alternatives.hasNext }

    val pathToRoot: Sequence<ChoicePointContext>
        get() = sequence {
            var curr: ChoicePointContext? = this@ChoicePointContext
            while (curr != null) {
                yield(curr as ChoicePointContext)
                curr = curr.parent
            }
        }

    fun clone(
            alternatives: Cursor<out Any> = this.alternatives,
            executionContext: ExecutionContext? = this.executionContext,
            parent: ChoicePointContext? = this.parent,
            depth: Int = this.depth
    ): ChoicePointContext = when (this) {
        is Primitives -> Primitives(alternatives as Cursor<out Solve.Response>, executionContext, parent, depth)
        is Rules -> Rules(alternatives as Cursor<out Rule>, executionContext, parent, depth)
    }

    data class Primitives(
            override val alternatives: Cursor<out Solve.Response>,
            override val executionContext: ExecutionContext?,
            override val parent: ChoicePointContext?,
            override val depth: Int)
        : ChoicePointContext(alternatives, executionContext, parent, depth)

    data class Rules(
            override val alternatives: Cursor<out Rule>,
            override val executionContext: ExecutionContext?,
            override val parent: ChoicePointContext?,
            override val depth: Int)
        : ChoicePointContext(alternatives, executionContext, parent, depth) {
    }
}

fun ChoicePointContext?.nextDepth(): Int = if (this == null) 0 else this.depth + 1

fun ChoicePointContext?.appendPrimitives(alternatives: Cursor<out Solve.Response>, executionContext: ExecutionContext? = null): ChoicePointContext? {
    return if (alternatives.isOver) this
    else
        ChoicePointContext.Primitives(alternatives, executionContext,this, nextDepth())
}

fun ChoicePointContext?.appendRules(alternatives: Cursor<out Rule>, executionContext: ExecutionContext? = null): ChoicePointContext? {
    return if (alternatives.isOver) this
        else
            ChoicePointContext.Rules(alternatives, executionContext,this, nextDepth())
}