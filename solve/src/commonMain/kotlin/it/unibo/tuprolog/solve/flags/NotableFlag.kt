package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term

interface NotableFlag {
    val name: String
    val defaultValue: Term

    val admissibleValues: Set<Term>

    fun isAdmissibleValue(value: Term): Boolean =
        value in admissibleValues

    fun toPair(): Pair<String, Term> = name to defaultValue
}