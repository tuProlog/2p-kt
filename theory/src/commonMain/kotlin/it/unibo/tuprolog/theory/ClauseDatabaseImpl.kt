package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.rete.ReteNode
import it.unibo.tuprolog.theory.rete.ReteTree
import kotlin.collections.List as KtList

internal class ClauseDatabaseImpl private constructor(private val reteTree: ReteNode<*, Clause>) : ClauseDatabase {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val clauses: KtList<Clause> by lazy { reteTree.indexedElements.toList() }

    override val rules: KtList<Rule> by lazy { super.rules.toList() }

    override val directives: KtList<Directive> by lazy { super.directives.toList() }


    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
            ClauseDatabaseImpl(clauses + checkClausesCorrect(clauseDatabase.clauses))

    override fun plus(clause: Clause): ClauseDatabase = super.plus(checkClauseCorrect(clause))


    override fun contains(clause: Clause): Boolean = reteTree.get(clause).any()

    override fun contains(head: Struct): Boolean = contains(Rule.of(head, Var.anonymous()))

    override fun contains(functor: String, arity: Int): Boolean =
            reteTree.get(Clause.of(Struct.of(functor, (1..arity).map { Var.anonymous() }), Var.anonymous())).any()


    override fun get(clause: Clause): Sequence<Clause> = reteTree.get(clause)

    override fun get(head: Struct): Sequence<Clause> = get(Rule.of(head, Var.anonymous()))

    override fun get(functor: String, arity: Int): Sequence<Clause> =
            reteTree.get(Clause.of(Struct.of(functor, (1..arity).map { Var.anonymous() }), Var.anonymous()))


    override fun assertA(clause: Clause): ClauseDatabase =
            ClauseDatabaseImpl(reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = true) })

    override fun assertZ(clause: Clause): ClauseDatabase =
            ClauseDatabaseImpl(reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = false) })

    override fun retract(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(ClauseDatabaseImpl(newTheory), retracted.asIterable())
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(ClauseDatabaseImpl(newTheory), retracted.asIterable())
        }
    }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    override fun toString(): String = clauses.joinToString(".\n", "", ".\n")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ClauseDatabaseImpl

        if (clauses != other.clauses) return false

        return true
    }

    override fun hashCode(): Int = clauses.hashCode()

    /** Utility method to check clause well-formed property */
    private fun checkClauseCorrect(clause: Clause) = clause.also {
        require(clause.isWellFormed) { "ClauseDatabase can contain only well formed clauses: this isn't $clause" }
    }

    /** Utility method to check more than one clause well-formed property */
    private fun checkClausesCorrect(clauses: Iterable<Clause>) = clauses.also {
        require(clauses.all { it.isWellFormed }) {
            "ClauseDatabase can contain only well formed clauses: these aren't ${clauses.filterNot { it.isWellFormed }.toList()}"
        }
    }
}