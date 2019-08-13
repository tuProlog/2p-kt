package it.unibo.tuprolog.solve.solver.statemachine

/** This type represents how the state machine will see time instants */
internal typealias TimeInstant = Long

/** This type represents how the state machine will see time duration */
internal typealias TimeDuration = Long

/** A function returning current Time instant */
internal expect fun currentTime(): TimeInstant
