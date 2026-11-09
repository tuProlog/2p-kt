package it.unibo.tuprolog.solve.timing

import kotlin.js.JsName
import kotlin.time.Duration

/**
 * What a [TimedSolutions] measured so far (#1010). Times spent inside the solver (in `hasNext()`/`next()` calls, and
 * creating the iterator) are *active*; [elapsed] also includes the consumer's pauses between those calls.
 */
data class ResolutionTiming(
    /** From the start of iteration to the first solution, or `null` if there was none yet. */
    @JsName("firstAnswer")
    val firstAnswer: Duration?,
    /** The active time spent producing each solution, the first included, in order. */
    @JsName("advances")
    val advances: List<Duration>,
    /** All active time, including the time spent finding out that there are no more solutions. */
    @JsName("active")
    val active: Duration,
    /** From the start of iteration to the end of the last call into the solver: includes the consumer's pauses. */
    @JsName("elapsed")
    val elapsed: Duration,
    /** The number of solutions produced. */
    @JsName("answers")
    val answers: Int,
    @JsName("outcome")
    val outcome: Outcome,
)
