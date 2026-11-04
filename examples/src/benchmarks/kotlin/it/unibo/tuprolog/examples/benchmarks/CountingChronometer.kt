package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.utils.time.Chronometer
import kotlin.time.Duration

/**
 * A [Chronometer] decorating [delegate], counting its leaps and forced checks into [counter], together with those of
 * its child scopes (i.e. of nested resolutions).
 */
class CountingChronometer(
    private val delegate: Chronometer,
    val counter: LeapCounter = LeapCounter(),
) : Chronometer by delegate {
    override fun leap(): Boolean {
        counter.leaps++
        return delegate.leap()
    }

    override fun checkNow(): Boolean {
        counter.checks++
        return delegate.checkNow()
    }

    override fun childScope(limit: Duration): Chronometer = CountingChronometer(delegate.childScope(limit), counter)
}
