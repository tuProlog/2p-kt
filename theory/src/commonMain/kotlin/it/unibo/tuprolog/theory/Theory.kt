package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

internal object Theory {

    /** Utility method to check clause well-formed property */
    fun checkClauseCorrect(clause: Clause) =
        clause.also {
            require(clause.isWellFormed) { "ClauseDatabase can contain only well formed clauses: " +
                    "this isn't $clause" }
        }

    /** Utility method to check more than one clause well-formed property */
    fun checkClausesCorrect(clauses: Iterable<Clause>) =
        clauses.also {
            require(clauses.all { it.isWellFormed }) {
                "ClauseDatabase can contain only well formed clauses: these aren't " +
                        "${clauses.filterNot { it.isWellFormed }.toList()}"
            }
        }

}