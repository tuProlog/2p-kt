package it.unibo.tuprolog.solve.exception.warning

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.js.JsName

class InitializationIssue(
    @JsName("goal") val goal: Struct,
    override val cause: TuPrologRuntimeException? = null,
    contexts: Array<ExecutionContext>
) : PrologWarning("Error while solving initialization goal $goal: ${cause ?: "failure"}", cause, contexts) {

    constructor(
        goal: Struct,
        cause: TuPrologRuntimeException? = null,
        context: ExecutionContext
    ) : this(goal, cause, arrayOf(context))

    override fun updateContext(newContext: ExecutionContext): PrologWarning =
        InitializationIssue(goal, cause, contexts.setFirst(newContext))

    override fun pushContext(newContext: ExecutionContext): PrologWarning =
        InitializationIssue(goal, cause, contexts.addLast(newContext))
}
