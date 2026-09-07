package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * An implementation-specific flag controlling whether the solver performs last-call (tail-call) optimization, i.e.
 * avoids growing its internal execution-context stack on genuine tail calls. Defaults to [ON].
 */
@Suppress("MemberVisibilityCanBePrivate")
object LastCallOptimization : NotableFlag {
    /** Tail-call optimization is enabled. */
    @JvmField
    val ON = Atom.of("on")

    /** Tail-call optimization is disabled. */
    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "last_call_optimization"

    override val defaultValue: Term
        get() = ON

    override val admissibleValues: Sequence<Term> = sequenceOf(ON, OFF)
}
