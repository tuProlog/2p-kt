package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Term

data class Request(
        val signature: Signature,
        val arguments: List<Term>
)