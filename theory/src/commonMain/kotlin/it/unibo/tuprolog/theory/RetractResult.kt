package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/** A result given after a "retract" operation */
sealed class RetractResult {

    /** The result always present value, is the clause database resulting from the operation execution */
    @JsName("clauseDatabase")
    abstract val clauseDatabase: ClauseDatabase

    /** A successful "retract" operation result, carrying the new [clauseDatabase] and removed [clauses] */
    // TODO use lists in place of Iterables
    data class Success(
        override val clauseDatabase: ClauseDatabase,
        @JsName("clauses") val clauses: Iterable<Clause>
    ) : RetractResult() {

        /** Gets the first successfully retracted clause */
        @JsName("firstClause")
        val firstClause: Clause
            get() = clauses.first()
    }

    /** A failed "retract" operation result, carrying the unchanged [clauseDatabase] */
    data class Failure(override val clauseDatabase: ClauseDatabase) : RetractResult()
}