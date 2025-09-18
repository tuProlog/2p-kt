package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/** A result given after a "retract" operation */
sealed class RetractResult<out T : Theory> {
    open val isSuccess: Boolean
        get() = false

    open val isFailure: Boolean
        get() = false

    /** The result always present value, is the clause database resulting from the operation execution */
    @JsName("theory")
    abstract val theory: T

    @JsName("clauses")
    abstract val clauses: Iterable<Clause>?

    /** Gets the first successfully retracted clause */
    @JsName("firstClause")
    abstract val firstClause: Clause?

    /** A successful "retract" operation result, carrying the new [theory] and removed [clauses] */
    data class Success<T : Theory>(
        override val theory: T,
        override val clauses: Iterable<Clause>,
    ) : RetractResult<T>() {
        override val isSuccess: Boolean
            get() = true

        override val firstClause: Clause
            get() = clauses.first()
    }

    /** A failed "retract" operation result, carrying the unchanged [theory] */
    data class Failure<T : Theory>(
        override val theory: T,
    ) : RetractResult<T>() {
        override val isFailure: Boolean
            get() = true

        override val clauses: Nothing?
            get() = null

        override val firstClause: Nothing?
            get() = null
    }
}
