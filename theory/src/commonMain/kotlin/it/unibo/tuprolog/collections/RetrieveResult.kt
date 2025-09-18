package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

sealed class RetrieveResult<C : ClauseCollection> {
    open val isSuccess: Boolean
        get() = false

    open val isFailure: Boolean
        get() = false

    @JsName("collection")
    abstract val collection: C

    @JsName("clauses")
    abstract val clauses: List<Clause>?

    @JsName("firstClause")
    abstract val firstClause: Clause?

    data class Success<C : ClauseCollection>(
        override val collection: C,
        override val clauses: List<Clause>,
    ) : RetrieveResult<C>() {
        override val isSuccess: Boolean
            get() = true

        override val firstClause: Clause
            get() = clauses.first()
    }

    data class Failure<C : ClauseCollection>(
        override val collection: C,
    ) : RetrieveResult<C>() {
        override val isFailure: Boolean
            get() = true

        override val clauses: Nothing?
            get() = null

        override val firstClause: Nothing?
            get() = null
    }
}
