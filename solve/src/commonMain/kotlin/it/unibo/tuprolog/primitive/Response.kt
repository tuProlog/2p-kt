package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Signature

data class Response(
        val signature: Signature,
        val arguments: List<Term>
        /* TODO add any contextual data or callback here*/
)