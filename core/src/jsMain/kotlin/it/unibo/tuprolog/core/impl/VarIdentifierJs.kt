package it.unibo.tuprolog.core.impl

internal actual typealias VarIdentifier = Double

// JS is single-threaded: a plain number suffices
private var counter = 0.0

internal actual fun nextVarIdentifier(): VarIdentifier = counter++
