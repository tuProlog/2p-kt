package it.unibo.tuprolog.solve.timing

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * [solutions], timed on [timeSource] as they are consumed: see [timing]. Setting up the solver and the query happens
 * before, so it is up to the caller to time it. It can be iterated once, by one consumer at a time.
 */
class TimedSolutions internal constructor(
    private val solutions: Sequence<Solution>,
    private val timeSource: TimeSource.WithComparableMarks,
) : Sequence<Solution> {
    private var start: ComparableTimeMark? = null
    private var lastExit: ComparableTimeMark? = null
    private var active = Duration.ZERO
    private var sinceLastAnswer = Duration.ZERO
    private val advances = mutableListOf<Duration>()
    private var firstAnswer: Duration? = null
    private var exhausted = false
    private var halted = false

    /** What was measured so far: it can be read at any time, also while iterating. */
    @JsName("timing")
    val timing: ResolutionTiming
        get() =
            ResolutionTiming(
                firstAnswer = firstAnswer,
                advances = advances.toList(),
                active = active,
                elapsed = start?.let { lastExit!! - it } ?: Duration.ZERO,
                answers = advances.size,
                outcome =
                    when {
                        halted -> Outcome.HALTED
                        exhausted -> Outcome.EXHAUSTED
                        else -> Outcome.ABANDONED
                    },
            )

    private inline fun <T> measure(block: () -> T): T {
        val enter = timeSource.markNow()
        try {
            return block()
        } finally {
            val exit = timeSource.markNow()
            val duration = exit - enter
            active += duration
            sinceLastAnswer += duration
            lastExit = exit
        }
    }

    override fun iterator(): Iterator<Solution> {
        check(start == null) { "TimedSolutions can be iterated only once" }
        start = timeSource.markNow().also { lastExit = it }
        val iterator = measure { solutions.iterator() }
        return object : Iterator<Solution> {
            override fun hasNext(): Boolean = !exhausted && measure { iterator.hasNext() }.also { exhausted = !it }

            override fun next(): Solution {
                val solution = measure { iterator.next() }
                advances += sinceLastAnswer
                sinceLastAnswer = Duration.ZERO
                if (firstAnswer == null) firstAnswer = lastExit!! - start!!
                halted = solution is Solution.Halt
                return solution
            }
        }
    }
}

/**
 * Times these solutions on [timeSource] as they are consumed (#1010): see [TimedSolutions.timing]. The result does not
 * close them: when they come from a [it.unibo.tuprolog.solve.SolutionStream], the caller still closes that.
 */
@JsName("timed")
@JvmOverloads
fun Sequence<Solution>.timed(timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic): TimedSolutions =
    TimedSolutions(this, timeSource)
