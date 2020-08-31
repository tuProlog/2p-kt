package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term

interface NotableFlag {
    val name: String
    val defaultValue: Term

    val admissibleValues: Sequence<Term>

    fun isAdmissibleValue(value: Term): Boolean =
        value in admissibleValues

    fun toPair(): Pair<String, Term> =
        this to defaultValue

    infix fun to(value: Term): Pair<String, Term> =
        name to (value.also {
            require(isAdmissibleValue(it)) {
                "$value is not an admissible value for flag $name"
            }
        })
}