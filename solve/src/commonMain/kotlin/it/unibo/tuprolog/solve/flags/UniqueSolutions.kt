package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether a solver's output solution sequence may contain multiple
 * `yes` solutions carrying the same solved-query term (e.g. `member(X, [1, 1, 1])` yielding three identical `yes`
 * solutions with `X = 1`). Defaults to [OFF] (unaffected, standard behaviour); set to [ON] to keep only the first
 * `yes` solution for each distinct solved-query term encountered, dropping any later duplicate.
 */
@Suppress("MemberVisibilityCanBePrivate")
object UniqueSolutions : NotableFlag {
    /** Duplicate `yes` solutions (by solved-query term) are dropped. */
    @JvmField
    val ON = Atom.of("on")

    /** Every `yes` solution is emitted, even if its solved-query term repeats an earlier one. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "unique_solutions"

    override val defaultValue: Term
        get() = OFF

    override val admissibleValues = FlagDomain.Enumerated(ON, OFF)
}
