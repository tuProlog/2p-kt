package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of '=@=' predicate */
/**True if Term1 is equivalent to Term2. A variable is only identical to a sharing variable.**/

object StandardOrderEqual : StandardOrderRelation<ExecutionContext>("=@=") {
    override fun standardOrderfunction(x: Term, y: Term): Boolean {
        return x.compareTo(y) == 0
    }
}
