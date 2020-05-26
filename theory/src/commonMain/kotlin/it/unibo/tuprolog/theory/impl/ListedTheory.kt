package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import kotlin.collections.List as KtList

internal class ListedTheory
private constructor(
    override val clauses: KtList<Clause>
) : AbstractTheory() {

    constructor(clauses: Iterable<Clause>) : this(clauses.toList()) {
        checkClausesCorrect(clauses)
    }

    override fun plus(theory: Theory): Theory =
        ListedTheory(
            clauses.asIterable() + checkClausesCorrect(
                theory.clauses
            )
        )

    override fun get(clause: Clause): Sequence<Clause> =
        clauses.filter { it matches clause }.asSequence()

    override fun assertA(clause: Clause): Theory =
        ListedTheory(
            listOf(
                checkClauseCorrect(
                    clause
                )
            ) + clauses
        )

    override fun assertZ(clause: Clause): Theory =
        ListedTheory(
            clauses + listOf(
                checkClauseCorrect(clause)
            )
        )

    override fun retract(clause: Clause): RetractResult {
        val retractability = clauses.filter { it matches clause }

        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val toBeActuallyRetracted = retractability.first()
                val newTheory = clauses.filter { it != toBeActuallyRetracted }
                RetractResult.Success(
                    ListedTheory(
                        newTheory
                    ), listOf(toBeActuallyRetracted)
                )
            }
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val retractability = clauses.filter { it matches clause }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val partitionedClauses = clauses.toList().partition { it matches clause }
                val newTheory = partitionedClauses.second
                val toBeActuallyRetracted = partitionedClauses.first
                RetractResult.Success(
                    ListedTheory(
                        newTheory
                    ), toBeActuallyRetracted
                )
            }
        }
    }
}