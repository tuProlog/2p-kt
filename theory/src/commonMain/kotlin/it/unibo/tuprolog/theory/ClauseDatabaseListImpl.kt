package it.unibo.tuprolog.theory
import it.unibo.tuprolog.core.*
import kotlin.collections.List as KtList

internal class ClauseDatabaseListImpl private constructor (private val clauseList : KtList<Clause>) : AbstractClauseDatabase() {

    constructor(clauses: Iterable<Clause>) : this(clauses.toList()) {
        checkClausesCorrect(clauses)
    }

    override val clauses: Iterable<Clause> by lazy { clauseList.asIterable() }

    override fun plus(clauseDatabase: ClauseDatabase): ClauseDatabase =
        ClauseDatabaseListImpl(clauses.asIterable() + clauseDatabase.asIterable())

    override fun get(clause: Clause): Sequence<Clause> =
        clauses.filter { it.structurallyEquals(clause) }.asSequence()

    override fun assertA(clause: Clause): ClauseDatabase =
        ClauseDatabaseListImpl(listOf(checkClauseCorrect(clause)) + clauseList)


    override fun assertZ(clause: Clause): ClauseDatabase =
        ClauseDatabaseListImpl(clauseList + listOf(checkClauseCorrect(clause)))


    override fun retract(clause: Clause): RetractResult {
        val retractability = clauses.filter { it.structurallyEquals(clause) }

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
        val retractability = clauses.filter { it.structurallyEquals(clause) }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val partitionedClauses = clauses.toList().partition { it.structurallyEquals(clause) }
                val newTheory = partitionedClauses.first
                val toBeActuallyRetracted = partitionedClauses.second
                RetractResult.Success(ClauseDatabaseListImpl(newTheory), toBeActuallyRetracted)
            }
        }
    }

    /*override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ClauseDatabaseListImpl

        if (clauses != other.clauses) return false

        return true
    }*/

}