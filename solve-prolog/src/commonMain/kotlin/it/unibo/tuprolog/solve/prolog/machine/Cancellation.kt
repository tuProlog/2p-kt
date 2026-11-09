package it.unibo.tuprolog.solve.prolog.machine

import kotlin.concurrent.Volatile

/**
 * The cancellation of a resolution started by `solve` (#1010): the machines answering it, and those of the nested
 * resolutions started from it (`Request.subSolver()`), check [isCancelled] at every step. A nested stream's own
 * cancellation also honours its [parent]'s.
 */
internal class Cancellation(
    private val parent: Cancellation? = null,
) {
    @Volatile
    private var cancelled = false

    val isCancelled: Boolean
        get() = cancelled || parent?.isCancelled == true

    fun cancel() {
        cancelled = true
    }
}
