package it.unibo.tuprolog.core.impl

import java.util.concurrent.atomic.AtomicLong

internal actual typealias VarIdentifier = Long

private val counter = AtomicLong()

internal actual fun nextVarIdentifier(): VarIdentifier = counter.getAndIncrement()
