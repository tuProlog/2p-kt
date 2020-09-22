package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/** A result given after a "retract" operation */
sealed class RetractResult {

    /** The result always present value, is the clause database resulting from the operation execution */
    @JsName("theory")
    abstract val theory: Theory

    /** A successful "retract" operation result, carrying the new [theory] and removed [clauses] */
    data class Success(
        override val theory: Theory,
        @JsName("clauses") val clauses: Iterable<Clause>
    ) : RetractResult() {

        /** Gets the first successfully retracted clause */
        @JsName("firstClause")
        val firstClause: Clause
            get() = clauses.first()
    }

    /** A failed "retract" operation result, carrying the unchanged [theory] */
    data class Failure(override val theory: Theory) : RetractResult()
}
