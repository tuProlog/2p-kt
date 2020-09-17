package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

sealed class RetrieveResult<C : ClauseCollection> {

    @JsName("collection")
    abstract val collection: C

    data class Success<C : ClauseCollection>(
        override val collection: C,
        @JsName("clauses") val clauses: List<Clause>
    ) : RetrieveResult<C>() {

        @JsName("firstClause")
        val firstClause: Clause
            get() = clauses.first()
    }

    data class Failure<C : ClauseCollection>(override val collection: C) : RetrieveResult<C>()
}
