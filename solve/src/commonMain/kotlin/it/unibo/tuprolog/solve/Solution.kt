package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

sealed class Solution {


    data class Yes(override val query: Struct, override val solution: Struct, override val substitution: Substitution.Unifier) : Solution()

    data class No(override val query: Struct) : Solution() {

        override val solution: Struct? = null

        override val substitution: Substitution = Substitution.failed()

    }


    abstract val query: Struct
    abstract val solution: Struct?
    abstract val substitution: Substitution
}