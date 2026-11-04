package it.unibo.tuprolog.utils.time.impl

import kotlin.time.Duration

/** Blocks the calling thread for (at least) [duration], according to the platform's monotonic clock. */
internal expect fun blockFor(duration: Duration)
