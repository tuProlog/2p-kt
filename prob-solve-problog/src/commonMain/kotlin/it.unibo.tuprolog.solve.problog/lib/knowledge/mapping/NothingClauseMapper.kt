package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.core.Clause

internal object NothingClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean {
        return true
    }

    override fun apply(clause: Clause): List<Clause> {
        return listOf(clause)
    }
}
