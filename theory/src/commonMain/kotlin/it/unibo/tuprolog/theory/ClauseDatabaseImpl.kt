package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.rete.ReteNode
import it.unibo.tuprolog.theory.rete.clause.ReteTree
import kotlin.collections.List as KtList

internal class ClauseDatabaseImpl private constructor(private val reteTree: ReteNode<*, Clause>) : AbstractClauseDatabase() {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val clauses: KtList<Clause> by lazy { reteTree.indexedElements.toList() }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        ClauseDatabaseImpl(clauses + checkClausesCorrect(clauseDatabase.clauses))

    override fun get(clause: Clause): Sequence<Clause> = reteTree.get(clause)

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
}