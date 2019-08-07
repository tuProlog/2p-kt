package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

/** A class representing a solution to a goal */
sealed class Solution {

    /** The query to which the solution refers */
    abstract val query: Struct
    /** The Struct representing the solution, or `null` if no solution is available */
    abstract val solution: Struct?
    /** The substitution that has been applied to find the solution, or a `failed` substitution */
    abstract val substitution: Substitution

    /** A class representing the successful solution */
    data class Yes(
            override val query: Struct,
            /** The Struct representing the solution */
            override val solution: Struct,
            /** The successful substitution applied finding the solution */
            override val substitution: Substitution.Unifier
    ) : Solution()

    /** A class representing a failed solution */
    data class No(override val query: Struct) : Solution() {
        /** Always `null` because no solution was found */
        override val solution: Struct? = null
        /** Always [Substitution.Fail] because no solution was found */
        override val substitution: Substitution.Fail = Substitution.failed()
    }
}