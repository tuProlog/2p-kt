package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List as KtList

interface ClauseDatabase : Iterable<Clause> {

    val rules: KtList<Rule>
        get() = clauses.filterIsInstance<Rule>()

    val directives: KtList<Directive>
        get() = clauses.filterIsInstance<Directive>()

    val clauses: KtList<Clause>

    operator fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase
    operator fun plus(clause: Clause): ClauseDatabase {
        return assertZ(clause)
    }

    operator fun contains(clause: Clause): Boolean
    operator fun contains(head: Struct): Boolean

    operator fun get(clause: Clause): Sequence<Clause>
    operator fun get(head: Struct): Sequence<Clause>

    fun assertA(clause: Clause): ClauseDatabase

    fun assertA(struct: Struct): ClauseDatabase {
        return assertA(Fact.of(struct))
    }

    fun assertZ(clause: Clause): ClauseDatabase

    fun assertZ(struct: Struct): ClauseDatabase {
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
        fun of(vararg clause: Clause): ClauseDatabase {
            return ClauseDatabaseImpl(listOf(*clause))
        }

        fun of(clauses: KtList<Clause>): ClauseDatabase {
            return ClauseDatabaseImpl(clauses)
        }
    }
}