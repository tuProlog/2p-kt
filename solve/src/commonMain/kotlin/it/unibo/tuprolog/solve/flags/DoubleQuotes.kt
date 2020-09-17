package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

@Suppress("MemberVisibilityCanBePrivate")
object DoubleQuotes : NotableFlag {

    val ATOM = Atom.of("atom")

    override val name: String = "double_quotes"

    override val defaultValue: Term
        get() = ATOM

    override val admissibleValues: Sequence<Term> = sequenceOf(ATOM)
}
