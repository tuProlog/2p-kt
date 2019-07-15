package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

sealed class RetractResult {

    data class Success(override val clauseDatabase: ClauseDatabase, val clauses: List<Clause>): RetractResult() {

        val clause: Clause
            get() = clauses[0]
    }

    data class Failure(override val clauseDatabase: ClauseDatabase): RetractResult()

    abstract val clauseDatabase: ClauseDatabase
}