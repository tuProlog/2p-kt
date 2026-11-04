package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.Chronometer
import kotlin.time.Duration

/** What a child chronometer was scoped by: its [parent], and the [limit] it was requested with. */
internal data class ChildScope(
    val parent: Chronometer,
    val limit: Duration,
)
