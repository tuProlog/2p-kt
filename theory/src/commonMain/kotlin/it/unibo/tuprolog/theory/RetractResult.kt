package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/**
 * The outcome of a [Theory.retract]/[Theory.retractAll] operation: either [Success], carrying the resulting
 * [theory] and the [clauses] that were actually removed, or [Failure], carrying the theory unchanged because no
 * clause matched. Modelled as a sealed hierarchy (rather than, say, a nullable result) so that callers must
 * handle both cases explicitly, mirroring how Prolog's own `retract/1` can succeed or simply fail.
 *
 * ```kotlin
 * when (val result = theory.retract(someClause)) {
 *     is RetractResult.Success -> useNewTheory(result.theory)
 *     is RetractResult.Failure -> reportNothingRemoved()
 * }
 * ```
 */
sealed class RetractResult<out T : Theory> {
    /** Whether the retract operation removed at least one clause. */
    open val isSuccess: Boolean
        get() = false

    /** Whether the retract operation removed no clause at all. */
    open val isFailure: Boolean
        get() = false

    /** The result always present value, is the clause database resulting from the operation execution */
    @JsName("theory")
    abstract val theory: T

    /** The clauses that were actually removed, in removal order; `null` if the operation [isFailure]. */
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

        /** @throws NoSuchElementException if [clauses] is empty */
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
