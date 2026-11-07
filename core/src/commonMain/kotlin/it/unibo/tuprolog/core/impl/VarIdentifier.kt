package it.unibo.tuprolog.core.impl

/**
 * The identifier of a [VarImpl]: a number, unique among all the variables created by the process, as the cheapest
 * such number on each platform. A `Long` on the JVM, a `Double` (exact up to 2^53) on JS, where `Long` is emulated
 * by an object per value.
 */
internal expect class VarIdentifier

/** A fresh [VarIdentifier], never returned before. */
internal expect fun nextVarIdentifier(): VarIdentifier
