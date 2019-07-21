package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

data class Request(
        val signature: Signature,
        val arguments: List<Term>,
        val context: ExecutionContext
)