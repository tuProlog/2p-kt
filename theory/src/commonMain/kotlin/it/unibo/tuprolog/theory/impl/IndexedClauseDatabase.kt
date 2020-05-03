package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.theory.AbstractClauseDatabase
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory.checkClauseCorrect
import it.unibo.tuprolog.theory.Theory.checkClausesCorrect

internal class IndexedClauseDatabase private constructor(private val reteTree: ReteNode<*, Clause>) : AbstractClauseDatabase() {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofList(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val clauses: Iterable<Clause> by lazy { reteTree.indexedElements.toList() }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        IndexedClauseDatabase(
            clauses + checkClausesCorrect(
                clauseDatabase.clauses
            )
        )

    override fun get(clause: Clause): Sequence<Clause> = reteTree.get(clause)

    override fun assertA(clause: Clause): ClauseDatabase =
        IndexedClauseDatabase(
            reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = true) })

    override fun assertZ(clause: Clause): ClauseDatabase =
        IndexedClauseDatabase(
            reteTree.deepCopy().apply { put(checkClauseCorrect(clause), beforeOthers = false) })

    override fun retract(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedClauseDatabase(
                    newTheory
                ), retracted.asIterable()
            )
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = reteTree.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedClauseDatabase(
                    newTheory
                ), retracted.asIterable()
            )
        }
    }
}