package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of '@<=' predicate */
/**True if both terms are equal or Term1 is before Term2 in the standard order of terms.**/

object StandardOrderLowerThanOrEqualTo :  StandardOrderRelation<ExecutionContext>("@<=") {
    override fun standardOrderfunction(x: Term, y: Term): Boolean {
        return x <= y;
    }
}