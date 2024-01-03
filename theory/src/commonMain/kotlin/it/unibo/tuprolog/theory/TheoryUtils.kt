package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

internal object TheoryUtils {
    /** Utility method to check clause well-formed property */
    fun checkClauseCorrect(clause: Clause) =
        clause.also {
            require(clause.isWellFormed) {
                "ClauseDatabase can contain only well formed clauses: " +
                    "this isn't $clause"
            }
        }

    fun checkClausesCorrect(vararg clauses: Clause) = clauses.asSequence().also { actualCheck(it) }

    private fun actualCheck(clauses: Sequence<Clause>) {
        require(clauses.all { it.isWellFormed }) {
            "ClauseDatabase can contain only well formed clauses: these aren't " +
                "${clauses.filterNot { it.isWellFormed }.toList()}"
        }
    }

    /** Utility method to check more than one clause well-formed property */
    fun checkClausesCorrect(clauses: Iterable<Clause>) = clauses.also { actualCheck(it.asSequence()) }

    fun checkClausesCorrect(clauses: Sequence<Clause>) = clauses.also { actualCheck(it) }
}
