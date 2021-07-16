package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName

class InitializationIssue(
    @JsName("goal") val goal: Struct,
    override val cause: ResolutionException? = null,
    contexts: Array<ExecutionContext>
) : Warning("Error while solving initialization goal $goal: ${cause ?: "failure"}", cause, contexts) {

    constructor(
        goal: Struct,
        cause: ResolutionException? = null,
        context: ExecutionContext
    ) : this(goal, cause, arrayOf(context))

    override fun updateContext(newContext: ExecutionContext, index: Int): InitializationIssue =
        InitializationIssue(goal, cause, contexts.setItem(index, newContext))

    override fun updateLastContext(newContext: ExecutionContext): InitializationIssue =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): InitializationIssue =
        InitializationIssue(goal, cause, contexts.addLast(newContext))
}
