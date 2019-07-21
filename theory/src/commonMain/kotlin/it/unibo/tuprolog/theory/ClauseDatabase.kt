package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*

interface ClauseDatabase : Iterable<Clause> {

    /** All [Clause]s in this database */
    val clauses: Iterable<Clause>

    /** Only [clauses] that are [Rule]s */
    val rules: Iterable<Rule>
        get() = clauses.filterIsInstance<Rule>()

    /** Only [clauses] that are [Directive]s */
    val directives: Iterable<Directive>
        get() = clauses.filterIsInstance<Directive>()

    /** Adds given ClauseDatabase to this */
    operator fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase

    /** Adds given Clause to this ClauseDatabase */
    operator fun plus(clause: Clause): ClauseDatabase = assertZ(clause)

    /** Checks if given clause is contained in this database */
    operator fun contains(clause: Clause): Boolean

    /** Checks if given clause is present in this database */
    operator fun contains(head: Struct): Boolean

    /** Checks if a clauses exists in this database having the specified [functor] and [arity] */
    fun contains(functor: String, arity: Int): Boolean

    /** Retrieves matching clauses from this database */
    operator fun get(clause: Clause): Sequence<Clause>

    /** Retrieves matching clauses from this database */
    operator fun get(head: Struct): Sequence<Clause>

    /** Retrieves all clauses in this database having the specified [functor] and [arity] */
    operator fun get(functor: String, arity: Int): Sequence<Clause>

    /** Adds given clause before all other clauses in this database */
    fun assertA(clause: Clause): ClauseDatabase

    /** Adds given clause before all other clauses in this database */
    fun assertA(struct: Struct): ClauseDatabase = assertA(Fact.of(struct))

    /** Adds given clause after all other clauses in this database */
    fun assertZ(clause: Clause): ClauseDatabase

    /** Adds given clause after all other clauses in this database */
    fun assertZ(struct: Struct): ClauseDatabase = assertZ(Fact.of(struct))

    /** Tries to delete a matching clause from this database */
    fun retract(clause: Clause): RetractResult

    /** Tries to delete a matching clause from this database */
    fun retract(head: Struct): RetractResult = retract(Rule.of(head, Var.anonymous()))

    /** Tries to delete all matching clauses from this database */
    fun retractAll(clause: Clause): RetractResult

    /** Tries to delete all matching clauses from this database */
    fun retractAll(head: Struct): RetractResult = retractAll(Rule.of(head, Var.anonymous()))

    companion object {

        /** Creates a ClauseDatabase with given clauses */
        fun of(vararg clause: Clause): ClauseDatabase = ClauseDatabaseImpl(clause.toList())

        /** Creates a ClauseDatabase with given clauses */
        fun of(clauses: Iterable<Clause>): ClauseDatabase = ClauseDatabaseImpl(clauses.toList())
    }
}