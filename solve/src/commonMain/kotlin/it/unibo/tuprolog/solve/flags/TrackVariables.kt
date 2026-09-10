package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether the solver keeps track of variable names/bindings beyond what
 * is strictly needed to compute a [it.unibo.tuprolog.solve.Solution] (e.g. for richer debugging/inspection).
 * Defaults to [OFF].
 */
@Suppress("MemberVisibilityCanBePrivate")
object TrackVariables : NotableFlag {
    /** Variable tracking is enabled. */
    @JvmField
    val ON = Atom.of("on")

    /** Variable tracking is disabled. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "track_variables"

    override val defaultValue: Term
        get() = OFF

    override val admissibleValues: Sequence<Term> = sequenceOf(ON, OFF)
}
