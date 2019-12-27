package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError

class MessageError(cause: Throwable? = null, context: ExecutionContext, val content: Struct) :
    PrologError(content.toString(), cause, context, content) {

    override fun updateContext(newContext: ExecutionContext): PrologError =
        MessageError(cause, context, content)
}