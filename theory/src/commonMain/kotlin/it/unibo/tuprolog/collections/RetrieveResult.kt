package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/**
 * The outcome of a [ClauseCollection.retrieve]/[ClauseCollection.retrieveAll] (or [ClauseQueue.retrieveFirst])
 * operation: either [Success], carrying the resulting [collection] and the [clauses] that were actually removed
 * from it, or [Failure], carrying the collection unchanged because no clause matched. This is the
 * [ClauseCollection]-level counterpart of `it.unibo.tuprolog.theory.RetractResult`, which wraps it to implement
 * [it.unibo.tuprolog.theory.Theory.retract].
 */
sealed class RetrieveResult<C : ClauseCollection> {
    /** Whether the operation removed at least one clause. */
    open val isSuccess: Boolean
        get() = false

    /** Whether the operation removed no clause at all. */
    open val isFailure: Boolean
        get() = false

    /** The clause collection resulting from the operation (unchanged, if it [isFailure]). */
    @JsName("collection")
    abstract val collection: C

    /** The clauses that were actually removed, in removal order; `null` if the operation [isFailure]. */
    @JsName("clauses")
    abstract val clauses: List<Clause>?

    /** The first successfully removed clause; `null` if the operation [isFailure]. */
    @JsName("firstClause")
    abstract val firstClause: Clause?

    /** A successful retrieval result, carrying the resulting [collection] and removed [clauses]. */
    data class Success<C : ClauseCollection>(
        override val collection: C,
        override val clauses: List<Clause>,
    ) : RetrieveResult<C>() {
        override val isSuccess: Boolean
            get() = true

        /** @throws NoSuchElementException if [clauses] is empty */
        override val firstClause: Clause
            get() = clauses.first()
    }

    /** A failed retrieval result, carrying the unchanged [collection]. */
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
