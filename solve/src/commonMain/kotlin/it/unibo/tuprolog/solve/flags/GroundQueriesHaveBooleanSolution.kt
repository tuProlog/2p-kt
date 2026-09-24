package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling how a `yes` solution to a *ground* query (e.g. `2 is 1 + 1`, with
 * no variables at all) is presented to the user by a `SolutionFormatter`/UI. Defaults to [OFF] (unaffected,
 * standard behaviour: the solved query is still shown, e.g. `yes: 2 is 1 + 1`); set to [ON] to present it simply
 * as `yes.`, since the solved-query term would equal the query verbatim and carries no extra information.
 *
 * This only affects presentation -- the underlying [it.unibo.tuprolog.solve.Solution] is unchanged either way.
 */
@Suppress("MemberVisibilityCanBePrivate")
object GroundQueriesHaveBooleanSolution : NotableFlag {
    /** A ground query's `yes` solution is presented simply as `yes.`. */
    @JvmField
    val ON = Atom.of("on")

    /** A ground query's `yes` solution is presented with its solved query, as usual. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "ground_queries_have_boolean_solution"

    override val defaultValue: Term
        get() = OFF

    override val admissibleValues = FlagDomain.Enumerated(ON, OFF)
}
