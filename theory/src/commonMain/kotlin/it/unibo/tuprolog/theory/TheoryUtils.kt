package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

internal object TheoryUtils {
    /** Returns [clause], after checking it is [well-formed][Clause.isWellFormed]. */
    fun checkClauseCorrect(clause: Clause) =
        clause.also {
            require(clause.isWellFormed) { "A theory can only contain well-formed clauses: this isn't $clause" }
            // shortcut: pattern-headed clauses are rejected until procedures support variable functors (wildcard buckets)
            require(
                clause.head?.isPattern != true,
            ) { "Clauses whose head has a variable functor are not supported yet: $clause" }
        }
}

/** These clauses as a list, after checking each of them is well formed (in a single pass). */
internal fun Iterable<Clause>.validated(): List<Clause> = map { TheoryUtils.checkClauseCorrect(it) }
