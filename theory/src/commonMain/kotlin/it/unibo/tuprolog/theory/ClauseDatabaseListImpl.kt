package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import kotlin.collections.List as KtList

internal class ClauseDatabaseListImpl
    private constructor (
        override val clauses: KtList<Clause>
    ) : AbstractClauseDatabase() {

    constructor(clauses: Iterable<Clause>) : this(clauses.toList()) {
        checkClausesCorrect(clauses)
    }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        ClauseDatabaseListImpl(clauses.asIterable() + clauseDatabase.asIterable())

    override fun get(clause: Clause): Sequence<Clause> =
        //TODO check validity and performances impact
        clauses.filter { it matches clause }.asSequence()

    override fun assertA(clause: Clause): ClauseDatabase =
        ClauseDatabaseListImpl(listOf(checkClauseCorrect(clause)) + clauses)

    override fun assertZ(clause: Clause): ClauseDatabase =
        ClauseDatabaseListImpl(clauses + listOf(checkClauseCorrect(clause)))

    override fun retract(clause: Clause): RetractResult {
        //TODO check validity and performances impact
        val retractability = clauses.filter { it matches clause }

        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val toBeActuallyRetracted = retractability.first()
                val newTheory = clauses.filter { it == toBeActuallyRetracted }
                RetractResult.Success(ClauseDatabaseListImpl(newTheory), listOf(toBeActuallyRetracted))
            }
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        //TODO check validity and performances impact
        val retractability = clauses.filter { it matches clause }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                //TODO check validity and performances impact
                val partitionedClauses = clauses.toList().partition { it matches clause }
                val newTheory = partitionedClauses.first
                val toBeActuallyRetracted = partitionedClauses.second
                RetractResult.Success(ClauseDatabaseListImpl(newTheory), toBeActuallyRetracted)
            }
        }
    }
}