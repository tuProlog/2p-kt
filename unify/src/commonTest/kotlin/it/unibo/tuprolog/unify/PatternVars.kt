package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Var

/** Fresh variables per case, for the specs of patterns: `P` and `Q` mostly stand for functors. */
@Suppress("ktlint:standard:property-naming")
internal class PatternVars {
    val P = Var.of("P")
    val Q = Var.of("Q")
    val X = Var.of("X")
    val Y = Var.of("Y")
    val Z = Var.of("Z")
}
