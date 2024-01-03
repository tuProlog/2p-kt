package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

@Suppress("MemberVisibilityCanBePrivate")
object LastCallOptimization : NotableFlag {
    @JvmField
    val ON = Atom.of("on")

    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "last_call_optimization"

    override val defaultValue: Term
        get() = ON

    override val admissibleValues: Sequence<Term> = sequenceOf(ON, OFF)
}
