package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.concurrent.Volatile
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Common base of [Chronometer]s: keeps the start mark and the sticky expiration flag, and implements everything
 * but [leap] in terms of actual clock readings. [start] is taken from [timeline] unless given (e.g. by
 * [childScope], so that the parent's and the child's time agree), and [expired] lets a child be born expired.
 */
internal abstract class AbstractChronometer(
    final override val timeline: Timeline,
    final override val timeout: Duration,
    start: ComparableTimeMark?,
    expired: Boolean,
) : Chronometer {
    init {
        require(!timeout.isNegative()) { "The timeout can't be negative: $timeout" }
    }

    /** Whether there is no time limit at all. */
    protected val isUnlimited: Boolean = timeout.isInfinite()

    private var start: ComparableTimeMark = start ?: read()

    /** The mark this chronometer (re)started at. */
    protected val startMark: ComparableTimeMark
        get() = start

    @Volatile
    private var expired: Boolean = expired

    final override val isExpired: Boolean
        get() = expired

    /** Reads the clock. */
    protected fun read(): ComparableTimeMark = timeline.source.markNow()

    /** The time elapsed from the start to [mark]. */
    protected fun elapsedAt(mark: ComparableTimeMark): Duration = mark - start

    /** Whether the timeout has expired at [mark], recording it if so. */
    protected fun expiredAt(mark: ComparableTimeMark): Boolean {
        if (!expired && elapsedAt(mark) > timeout) {
            expired = true
        }
        return expired
    }

    override fun checkNow(): Boolean =
        when {
            expired -> true
            isUnlimited -> false
            else -> expiredAt(read())
        }

    final override fun elapsed(): Duration = elapsedAt(read())

    final override fun remaining(): Duration =
        if (isUnlimited) Duration.INFINITE else (timeout - elapsed()).coerceAtLeast(Duration.ZERO)

    final override fun sleep(duration: Duration): Boolean {
        require(!duration.isNegative()) { "Can't sleep for a negative duration: $duration" }
        if (checkNow()) return true
        val wakeUp = elapsed() + duration
        while (true) {
            val now = elapsed()
            if (now >= wakeUp) return checkNow()
            // waking up just past the deadline, as expiration is strict
            val untilExpiry = if (isUnlimited) Duration.INFINITE else timeout - now + 1.nanoseconds
            timeline.waitFor(minOf(wakeUp - now, untilExpiry))
            if (checkNow()) return true
        }
    }

    final override fun restart() {
        val now = read()
        start = now
        expired = false
        onRestart(now)
    }

    /** Hook for subclasses' own state, called by [restart] once restarted at [now]. */
    protected open fun onRestart(now: ComparableTimeMark) = Unit

    final override fun childScope(limit: Duration): Chronometer {
        require(!limit.isNegative()) { "The limit can't be negative: $limit" }
        val now = read()
        val left = if (isUnlimited) Duration.INFINITE else timeout - elapsedAt(now)
        return newScope(minOf(limit, left).coerceAtLeast(Duration.ZERO), now, expiredAt(now))
    }

    /** Creates a chronometer of this kind, on the same [timeline], with the given [timeout], [start] and [expired]. */
    protected abstract fun newScope(
        timeout: Duration,
        start: ComparableTimeMark,
        expired: Boolean,
    ): Chronometer

    override fun toString(): String =
        "${this::class.simpleName}(timeout=$timeout, expired=$expired, timeline=$timeline)"
}
