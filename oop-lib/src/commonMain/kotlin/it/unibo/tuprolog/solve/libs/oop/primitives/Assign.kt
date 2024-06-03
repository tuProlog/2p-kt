package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.oop
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

class Assign(oopContext: OOPContext) :
    TernaryRelation.Predicative<ExecutionContext>(FUNCTOR), OOPContext by oopContext {
    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
        third: Term,
    ): Boolean =
        catchingOopExceptions {
            val ref = getArgumentAsObjectRef(0)
            val value = dealiasIfNecessary(third)
            ref.oop.assign((second as Atom).value, value)
        }

    init {
        require(signature.arity == ARITY)
    }

    companion object {
        const val FUNCTOR = "assign"
        const val ARITY = 3
    }
}
