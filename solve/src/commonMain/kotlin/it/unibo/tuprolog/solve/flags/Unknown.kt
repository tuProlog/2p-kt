package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

@Suppress("MemberVisibilityCanBePrivate")
object Unknown : NotableFlag {

    @JvmField
    val ERROR = Atom.of("error")

    @JvmField
    val WARNING = Atom.of("warning")

    @JvmField
    val FAIL = Atom.of("fail")

    override val name: String = "unknown"

    override val defaultValue: Term
        get() = WARNING

    override val admissibleValues: Sequence<Term> = sequenceOf(ERROR, WARNING, FAIL)
}
