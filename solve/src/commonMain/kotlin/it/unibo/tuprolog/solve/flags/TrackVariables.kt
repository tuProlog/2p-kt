package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

@Suppress("MemberVisibilityCanBePrivate")
object TrackVariables : NotableFlag {

    @JvmField
    val ON = Atom.of("on")

    @JvmField
    val OFF = Atom.of("off")

    override val name: String = "track_variables"

    override val defaultValue: Term
        get() = OFF

    override val admissibleValues: Sequence<Term> = sequenceOf(ON, OFF)
}
