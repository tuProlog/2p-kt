package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Rule

interface Theory : Iterable<Clause> {

    val rules: List<Rule>
        get() = clauses.filterIsInstance<Rule>()

    val directives: List<Directive>
        get() = clauses.filterIsInstance<Directive>()

    val clauses: List<Clause>
        get() = toList()

    operator fun plus(theory: Theory): Theory
    operator fun plus(clause: Clause): Theory {
        return assertZ(clause)
    }

    operator fun contains(clause: Clause): Boolean
    operator fun contains(head: Struct): Boolean

    operator fun get(clause: Clause): Sequence<Clause>
    operator fun get(head: Struct): Sequence<Clause>

    fun assertA(clause: Clause): Theory
    fun assertZ(clause: Clause): Theory
    fun retract(clause: Clause): Theory
    fun retractAll(clause: Clause): Theory
}