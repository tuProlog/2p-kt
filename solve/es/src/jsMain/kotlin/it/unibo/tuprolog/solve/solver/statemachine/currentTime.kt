package it.unibo.tuprolog.solve.solver.statemachine

import kotlin.js.Date

/** A function returning current Time instant */
internal actual fun currentTime(): TimeInstant = Date.now().toLong()
