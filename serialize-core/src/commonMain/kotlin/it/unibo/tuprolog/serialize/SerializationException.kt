package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Signals that a [Term] could not be serialized, e.g. because the target format cannot represent
 * it.
 *
 * @param term the term that could not be serialized.
 * @param message the detail message.
 */
class SerializationException(
    term: Term,
    message: String = "Error while serialising $term",
) : TuPrologException(message) {
    companion object {
        /** Signals that [reference] (possibly nested in a larger term) cannot be portably serialized. */
        @JvmStatic
        @JsName("forReference")
        fun forReference(reference: Ref): SerializationException =
            SerializationException(
                reference,
                "Error while serialising $reference: references to host values cannot be portably serialized",
            )
    }
}
