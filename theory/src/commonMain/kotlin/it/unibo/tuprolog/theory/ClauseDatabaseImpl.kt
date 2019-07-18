package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List as KtList

internal class ClauseDatabaseImpl private constructor(private val reteTree: ReteTree<*>) : ClauseDatabase {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.of(clauses))


    override val clauses: KtList<Clause> by lazy { reteTree.clauses.toList() }

    override val rules: KtList<Rule> by lazy { super.rules.toList() }
    override val directives: KtList<Directive> by lazy { super.directives.toList() }


    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
            ClauseDatabaseImpl(clauses + clauseDatabase.clauses)

    override fun contains(clause: Clause): Boolean = reteTree.get(clause).any()
    override fun contains(head: Struct): Boolean = contains(Rule.of(head, Var.anonymous()))

    override fun get(clause: Clause): Sequence<Clause> = reteTree.get(clause)
    override fun get(head: Struct): Sequence<Clause> = get(Rule.of(head, Var.anonymous()))

    override fun assertA(clause: Clause): ClauseDatabase =
            ClauseDatabaseImpl(reteTree.clone().apply { put(clause, before = true) })

    override fun assertZ(clause: Clause): ClauseDatabase =
            ClauseDatabaseImpl(reteTree.clone().apply { put(clause, before = false) })

    override fun retract(clause: Clause): RetractResult {
        val newTheory = reteTree.clone()
        val retracted = newTheory.remove(clause).toList()

        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(ClauseDatabaseImpl(newTheory), retracted)
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = reteTree.clone()
        val retracted = newTheory.removeAll(clause).toList()

        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(ClauseDatabaseImpl(newTheory), retracted)
        }
    }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    override fun toString(): String = clauses.joinToString(".\n", "", ".\n")

}