package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.core.Clause

/**
 * ClauseMapper represents a mapping function used for internal Theory representation.
 * The idea is that a given [Clause] can be elaborated and mapped to one or more [Clause]s.
 */
internal interface ClauseMapper {

    /** Returns true if this mapping can be applied to [clause]. */
    fun isCompatible(clause: Clause): Boolean

    /** Applies this mapping to [clause]. */
    fun apply(clause: Clause): List<Clause>
}
