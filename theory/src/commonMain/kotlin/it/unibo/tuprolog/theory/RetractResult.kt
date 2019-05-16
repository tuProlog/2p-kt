package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

sealed class RetractResult {

    data class Success(override val theory: Theory, val clauses: List<Clause>): RetractResult() {

        val clause: Clause
            get() = clauses[0]
    }

    data class Failure(override val theory: Theory): RetractResult()

    abstract val theory: Theory
}