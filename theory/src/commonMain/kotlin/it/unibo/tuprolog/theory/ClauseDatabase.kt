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
        fun of(vararg clause: Clause): ClauseDatabase = ClauseDatabaseImpl(clause.asIterable())

        /** Creates a ClauseDatabase with given clauses */
        fun of(clauses: Iterable<Clause>): ClauseDatabase = ClauseDatabaseImpl(clauses.asIterable())

        /** Creates a ClauseDatabase with given clauses */
        fun of(clauses: Sequence<Clause>): ClauseDatabase = ClauseDatabaseImpl(clauses.asIterable())

        /**
         * A visitor to prepare Clauses for execution
         *
         * For example, the [Clause] `product(A) :- A, A` is stored in the database, after preparation for execution,
         * as the Term: `product(A) :- call(A), call(A)`
         */
        internal val defaultPreparationForExecutionVisitor = object : TermVisitor<Term> {
            override fun defaultValue(term: Term) = term

            override fun visit(term: Struct): Term = when {
                term is Clause -> visit(term)
                term.functor in Clause.notableFunctors && term.arity == 2 ->
                    Struct.of(term.functor, term.argsSequence.map { arg -> arg.accept(this) })

                else -> term
            }

            override fun visit(term: Clause): Term = Clause.of(term.head, visit(term.body))

            override fun visit(term: Var): Term = Struct.of("call", term)
        }
    }
}

/**
 * Prepares the receiver ClauseDatabase for execution, using the provided visitor
 *
 * For example, the [Clause] `product(A) :- A, A` is stored in the database, after preparation for execution,
 * as the Term: `product(A) :- call(A), call(A)`
 */
fun ClauseDatabase.prepareForExecution(): ClauseDatabase =
        ClauseDatabaseImpl(this.clauses.map { it.accept(ClauseDatabase.defaultPreparationForExecutionVisitor) as Clause })
