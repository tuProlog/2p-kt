package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

@Suppress("MemberVisibilityCanBePrivate")
object Unknown : NotableFlag {

    val ERROR = Atom.of("error")
    val WARNING = Atom.of("warning")
    val FAIL = Atom.of("fail")

    override val name: String = "unknown"

    override val defaultValue: Term
        get() = WARNING

    override val admissibleValues: Sequence<Term> = sequenceOf(ERROR, WARNING, FAIL)

}