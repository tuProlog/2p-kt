package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.normalized

internal object TheoryUtils {
    /**
     * Returns [clause], [normalized][Clause.normalized] unless tagged as [Clause.VERBATIM_BODY_TAG], after checking it
     * is [well-formed][Clause.isWellFormed].
     */
    fun checkClauseCorrect(clause: Clause): Clause {
        require(clause.isWellFormed) { "A theory can only contain well-formed clauses: this isn't $clause" }
        val normalized = clause.normalized()
        // the tag is only read for the rare clauses that normalization changes
        return if (normalized === clause || clause.tags[Clause.VERBATIM_BODY_TAG] == true) clause else normalized
    }
}

/** These clauses as a list, normalized, after checking each of them is well formed (in a single pass). */
internal fun Iterable<Clause>.validated(): List<Clause> = map { TheoryUtils.checkClauseCorrect(it) }
