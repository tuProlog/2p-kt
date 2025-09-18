package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause

/**
 * A [ClauseMapper] that applies not transformation to clauses.
 *
 * @author Jason Dellaluce
 * */
internal object NothingClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean = true

    override fun apply(clause: Clause): List<Clause> = listOf(clause)
}
