package it.unibo.tuprolog.solve

/** This type represents how the solver will see time instants */
typealias TimeInstant = Long

/** This type represents how the solver will see time duration */
typealias TimeDuration = Long

/** A function returning current Time instant */
expect fun currentTimeInstant(): TimeInstant
