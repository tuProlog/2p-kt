package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

@Suppress("MemberVisibilityCanBePrivate")
object DoubleQuotes : NotableFlag {
    @JvmField
    val ATOM = Atom.of("atom")

    override val name: String = "double_quotes"

    override val defaultValue: Term
        get() = ATOM

    override val admissibleValues: Sequence<Term> = sequenceOf(ATOM)
}
