@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.Unificator

/** The unification engines the benchmarks compare (#1020), by JMH `engine` parameter value; both strict. */
enum class UnificatorSetting(
    val id: String,
    val unificator: Unificator,
) {
    /** The equation-based engine (`AbstractUnificator`), the default before #1020. */
    EQUATIONAL(
        "equational",
        object : AbstractUnificator() {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = first == second
        },
    ),

    /** The worklist-based engine (`AbstractWorklistUnificator`). */
    WORKLIST("worklist", object : AbstractWorklistUnificator() {}),
    ;

    companion object {
        /** The [UnificatorSetting] whose [id] is [id]. */
        fun byId(id: String): UnificatorSetting = entries.first { it.id == id }
    }
}
