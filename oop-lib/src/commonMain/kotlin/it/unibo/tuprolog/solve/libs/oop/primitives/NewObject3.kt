package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object NewObject3 : TernaryRelation.Functional<ExecutionContext>("new_object") {

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsList(1)
        return catchingOopExceptions {
            val type = getArgumentAsTypeRef(0)
            val arguments = (second as List).toArray()
            val objectReference = type?.create(termToObjectConverter, *arguments)?.asObjectRef()
            objectReference?.let { mgu(it, third) } ?: Substitution.failed()
        }
    }
}
