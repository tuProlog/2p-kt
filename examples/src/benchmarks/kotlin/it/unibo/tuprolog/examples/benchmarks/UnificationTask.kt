package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.Unificator

/** One unification-benchmark operation (#1020), on terms or substitutions built beforehand. */
fun interface UnificationTask {
    fun runWith(
        unificator: Unificator,
        occursCheck: Boolean,
    ): Substitution
}
