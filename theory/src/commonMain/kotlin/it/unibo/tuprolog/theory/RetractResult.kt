package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/** A result given after a "retract" operation */
sealed class RetractResult<out T : Theory> {

    /** The result always present value, is the clause database resulting from the operation execution */
    @JsName("theory")
    abstract val theory: T

    /** A successful "retract" operation result, carrying the new [theory] and removed [clauses] */
    data class Success<T : Theory>(
        override val theory: T,
        @JsName("clauses") val clauses: Iterable<Clause>
    ) : RetractResult<T>() {

        /** Gets the first successfully retracted clause */
        @JsName("firstClause")
        val firstClause: Clause
            get() = clauses.first()
    }

    /** A failed "retract" operation result, carrying the unchanged [theory] */
    data class Failure<T : Theory>(override val theory: T) : RetractResult<T>()
}
