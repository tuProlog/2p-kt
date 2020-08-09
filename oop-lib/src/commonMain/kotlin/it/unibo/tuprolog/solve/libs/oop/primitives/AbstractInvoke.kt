package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

abstract class AbstractInvoke(suffix: String) : TernaryRelation.Functional<ExecutionContext>("invoke_$suffix") {

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsRef(0)
        ensuringArgumentIsStruct(1)

        val ref = first as Ref
        val method = second as Struct

        return when (val result = ref.invoke(method.functor, *method.args)) {
            is Result.Value -> third mguWith result.getInvocationResult()
            else -> Substitution.failed()
        }
    }

    protected abstract fun Result.Value.getInvocationResult(): Term

}