package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName

/**
 * A [Warning] reported when solving an initialization [goal] (i.e. a directive, or a clause loaded while
 * partitioning a [it.unibo.tuprolog.theory.Theory]'s static/dynamic clauses at knowledge-base load time) does not
 * succeed -- either because it failed outright (no [cause]) or because it halted with [cause].
 *
 * Raised instead of aborting the whole knowledge-base loading process, so that a single misbehaving directive does
 * not prevent the rest of the theory from being loaded.
 */
class InitializationIssue(
    @JsName("goal") val goal: Struct,
    override val cause: ResolutionException? = null,
    contexts: Array<ExecutionContext>,
) : Warning("Error while solving initialization goal $goal: ${cause ?: "failure"}", cause, contexts) {
    constructor(
        goal: Struct,
        cause: ResolutionException? = null,
        context: ExecutionContext,
    ) : this(goal, cause, arrayOf(context))

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): InitializationIssue = InitializationIssue(goal, cause, contexts.setItem(index, newContext))

    override fun updateLastContext(newContext: ExecutionContext): InitializationIssue =
        updateContext(
            newContext,
            contexts.lastIndex,
        )

    override fun pushContext(newContext: ExecutionContext): InitializationIssue =
        InitializationIssue(goal, cause, contexts.addLast(newContext))
}
