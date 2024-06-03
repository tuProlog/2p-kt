package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.oop
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

class NewObject3(oopContext: OOPContext) :
    TernaryRelation.Functional<ExecutionContext>(FUNCTOR), OOPContext by oopContext {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution =
        catchingOopExceptions {
            val type = getArgumentAsTypeRef(0)
            ensuringArgumentIsList(1)
            val arguments = (second as List).toArray()
            val objectReference = type.oop.create(*arguments).asObjectRef()
            objectReference?.let { mgu(it, third) } ?: Substitution.failed()
        }

    init {
        require(signature.arity == ARITY)
    }

    companion object {
        const val FUNCTOR = "new_object"
        const val ARITY = 3
    }
}
