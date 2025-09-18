package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

class SerializationException(
    term: Term,
) : TuPrologException("Error while serialising $term")
