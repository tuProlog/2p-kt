package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List

interface Theory : Iterable<Clause> {

    val rules: List<Rule>
        get() = clauses.filterIsInstance<Rule>()

    val directives: List<Directive>
        get() = clauses.filterIsInstance<Directive>()

    val clauses: List<Clause>

    operator fun plus(theory: Theory): Theory
    operator fun plus(clause: Clause): Theory {
        return assertZ(clause)
    }

    operator fun contains(clause: Clause): Boolean
    operator fun contains(head: Struct): Boolean

    operator fun get(clause: Clause): Sequence<Clause>
    operator fun get(head: Struct): Sequence<Clause>

    fun assertA(clause: Clause): Theory

    fun assertA(struct: Struct): Theory {
        return assertA(Fact.of(struct))
    }

    fun assertZ(clause: Clause): Theory

    fun assertZ(struct: Struct): Theory {
        return assertZ(Fact.of(struct))
    }

    fun retract(clause: Clause): RetractResult

    fun retract(head: Struct): RetractResult {
        return retract(Rule.of(head, Var.anonymous()))
    }

    fun retractAll(clause: Clause): RetractResult

    fun retractAll(head: Struct): RetractResult {
        return retractAll(Rule.of(head, Var.anonymous()))
    }

    companion object {
        fun of(vararg clause: Clause): Theory {
            return TheoryImpl(listOf(*clause))
        }

        fun of(clauses: List<Clause>): Theory {
            return TheoryImpl(clauses)
        }
    }
}