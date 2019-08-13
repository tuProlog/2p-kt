package it.unibo.tuprolog.solve.solver.statemachine

/** A function returning current Time instant */
internal actual fun currentTime(): TimeInstant = System.currentTimeMillis()