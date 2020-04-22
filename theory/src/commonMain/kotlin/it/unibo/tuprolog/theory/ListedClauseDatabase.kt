package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List as KtList

internal class ListedClauseDatabase
    private constructor (
        override val clauses: KtList<Clause> // nota: era inutile avere due property perchè le liste sono immutabili in kt
    ) : AbstractClauseDatabase() {

    constructor(clauses: Iterable<Clause>) : this(clauses.toList()) {
        checkClausesCorrect(clauses)
    }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        ListedClauseDatabase(clauses.asIterable() + clauseDatabase.asIterable())

    override fun get(clause: Clause): Sequence<Clause> =
        clauses.filter { it.structurallyEquals(clause) }.asSequence()

    override fun assertA(clause: Clause): ClauseDatabase =
        ListedClauseDatabase(listOf(checkClauseCorrect(clause)) + clauses)

    override fun assertZ(clause: Clause): ClauseDatabase =
        ListedClauseDatabase(clauses + listOf(checkClauseCorrect(clause)))

    override fun retract(clause: Clause): RetractResult {
        val retractability = clauses.filter { it.structurallyEquals(clause) }

        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val toBeActuallyRetracted = retractability.first()
                val newTheory = clauses.filter { it == toBeActuallyRetracted }
                RetractResult.Success(ListedClauseDatabase(newTheory), listOf(toBeActuallyRetracted))
            }
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val retractability = clauses.filter { it.structurallyEquals(clause) }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val partitionedClauses = clauses.toList().partition { it.structurallyEquals(clause) }
                val newTheory = partitionedClauses.first
                val toBeActuallyRetracted = partitionedClauses.second
                RetractResult.Success(ListedClauseDatabase(newTheory), toBeActuallyRetracted)
            }
        }
    }
}