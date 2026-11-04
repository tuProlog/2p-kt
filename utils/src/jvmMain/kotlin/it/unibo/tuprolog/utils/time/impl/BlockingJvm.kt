package it.unibo.tuprolog.utils.time.impl

import kotlin.time.Duration

private const val NANOS_PER_MILLI = 1_000_000L

internal actual fun blockFor(duration: Duration) {
    if (!duration.isPositive()) return
    if (duration.isInfinite()) {
        while (true) Thread.sleep(Long.MAX_VALUE)
    }
    val nanos = duration.inWholeNanoseconds
    Thread.sleep(nanos / NANOS_PER_MILLI, (nanos % NANOS_PER_MILLI).toInt())
}
