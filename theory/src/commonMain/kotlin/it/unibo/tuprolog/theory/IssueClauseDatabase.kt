package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.rete2.ReteNode
import it.unibo.tuprolog.theory.rete2.clause.ReteTree

internal class IssueClauseDatabase private constructor(private val reteTree: ReteNode<*, Clause>) : AbstractClauseDatabase() {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val clauses: List<Clause> by lazy { reteTree.indexedElements.toList() }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        IssueClauseDatabase(clauses + checkClausesCorrect(clauseDatabase.clauses))

    override fun get(clause: Clause): Sequence<Clause> = reteTree.get(clause)

    override fun assertA(clause: Clause): ClauseDatabase =
        IssueClauseDatabase(reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = true) })

    override fun assertZ(clause: Clause): ClauseDatabase =
        IssueClauseDatabase(reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = false) })

    override fun retract(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(IssueClauseDatabase(newTheory), retracted.asIterable())
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(IssueClauseDatabase(newTheory), retracted.asIterable())
        }
    }
}