package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

data class Response(
        val signature: Signature,
        val arguments: List<Term>,
        val context: ExecutionContext? = null
)