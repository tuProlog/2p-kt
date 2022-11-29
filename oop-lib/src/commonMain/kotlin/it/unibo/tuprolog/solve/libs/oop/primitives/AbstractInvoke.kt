package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

abstract class AbstractInvoke(suffix: String) : TernaryRelation.Functional<ExecutionContext>("invoke_$suffix") {

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsStruct(1)

        val method = second as Struct

        return catchingOopExceptions {
            when (first) {
                is Ref -> {
                    actuallyInvoke(first, method, third)
                }
                is Struct -> {
                    val ref = findRefFromAlias(first)
                    actuallyInvoke(ref, method, third)
                }
                else -> Substitution.failed()
            }
        }
    }

    private fun Solve.Request<ExecutionContext>.actuallyInvoke(ref: Ref, method: Struct, resultTerm: Term): Substitution {
        return when (val result = ref.invoke(termToObjectConverter, method.functor, method.args)) {
            is Result.Value -> mgu(resultTerm, result.getInvocationResult())
            else -> Substitution.failed()
        }
    }

    protected abstract fun Result.Value.getInvocationResult(): Term
}
