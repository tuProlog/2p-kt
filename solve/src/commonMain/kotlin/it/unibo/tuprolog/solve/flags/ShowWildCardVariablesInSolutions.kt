package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether wildcard variables (those whose name starts with `_`,
 * e.g. `_P`, per [it.unibo.tuprolog.core.Var.isWildcard]) appear in a [it.unibo.tuprolog.solve.Solution]'s
 * substitution. Defaults to [ON] (unaffected, standard behaviour); set to [OFF] to hide them, matching common
 * Prolog tooling convention (see issue #980).
 */
@Suppress("MemberVisibilityCanBePrivate")
object ShowWildCardVariablesInSolutions : NotableFlag {
    /** Wildcard variables are shown in solutions' substitutions. */
    @JvmField
    val ON = Atom.of("on")

    /** Wildcard variables are hidden from solutions' substitutions. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "show_wildcard_variables_in_solutions"

    override val defaultValue: Term
        get() = ON

    override val admissibleValues = FlagDomain.Enumerated(ON, OFF)
}
