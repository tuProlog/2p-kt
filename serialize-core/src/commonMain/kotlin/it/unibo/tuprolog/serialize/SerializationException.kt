package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Signals that a [Term] could not be serialized, e.g. because the target format cannot represent
 * it.
 *
 * @param term the term that could not be serialized.
 */
class SerializationException(
    term: Term,
) : TuPrologException("Error while serialising $term")
