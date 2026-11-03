package it.unibo.tuprolog.utils.time

import kotlin.time.Duration

/**
 * Tracks the time elapsed by a timed activity (e.g. a resolution process) against a [timeout], on a [timeline].
 *
 * A [Chronometer] is running from its creation (see [ChronometerFactory.create]) and is mutable: it is meant to
 * be owned by one activity, never shared among independent ones. The activity reports each elementary step it
 * performs through [leap], which tells whether the [timeout] has expired. Implementations differ in how often
 * they actually read the clock while doing so (see [ChronometerFactory]), but they all agree that:
 *
 * - only an actual clock reading can establish expiration: estimates never do;
 * - expiration is strict: the [timeout] has expired once the elapsed time is *greater than* it, so a
 *   [Duration.ZERO] timeout expires as soon as any time elapses, and an infinite one never does;
 * - expiration is sticky: once expired, a chronometer stays so (until [restart]ed);
 * - observing a chronometer ([elapsed], [remaining], [isExpired]) never makes its timeline advance.
 */
interface Chronometer {
    /** The timeline time is measured, and waited on, against. */
    val timeline: Timeline

    /** The time budget, from the start; [Duration.INFINITE] means no limit. */
    val timeout: Duration

    /** Whether expiration has already been established, without reading the clock. */
    val isExpired: Boolean

    /**
     * Registers one elementary step of the timed activity, returning `true` iff the [timeout] is known to have
     * expired. Implementations may or may not read the clock on each call.
     */
    fun leap(): Boolean

    /** Reads the clock, returning `true` iff the [timeout] has expired. */
    fun checkNow(): Boolean

    /** The time elapsed since the start, read from the clock. */
    fun elapsed(): Duration

    /** The time left before the [timeout] expires (never negative), read from the clock. */
    fun remaining(): Duration

    /**
     * Waits on the [timeline] for [duration], or until the [timeout] expires, whichever comes first, returning
     * `true` iff the [timeout] has expired.
     */
    fun sleep(duration: Duration): Boolean

    /**
     * Starts over: from now, the full [timeout] is available again, and expiration is forgotten. A child scope
     * (see [childScope]) restarts within its parent's deadline instead: its [timeout] becomes the least among the
     * limit it was created with and the parent's remaining time, and it is expired if the parent is.
     */
    fun restart()

    /**
     * Creates a chronometer for a sub-activity, on the same [timeline] and of the same kind, starting now, whose
     * deadline is the earliest among this chronometer's deadline and `now + limit`: a child can never extend its
     * parent's deadline.
     */
    fun childScope(limit: Duration = Duration.INFINITE): Chronometer
}
