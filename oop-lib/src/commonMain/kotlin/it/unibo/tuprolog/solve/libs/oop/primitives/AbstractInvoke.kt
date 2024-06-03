package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.oop
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

abstract class AbstractInvoke(
    suffix: String,
    private val oopContext: OOPContext,
) : TernaryRelation.Functional<ExecutionContext>("invoke_$suffix"), OOPContext by oopContext {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution {
        ensuringArgumentIsObjectRefOrAlias(0)
        ensuringArgumentIsStruct(1)

        val method = second as Struct

        return catchingOopExceptions {
            when (first) {
                is ObjectRef -> {
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

    private fun Solve.Request<ExecutionContext>.actuallyInvoke(
        ref: ObjectRef,
        method: Struct,
        resultTerm: Term,
    ): Substitution {
        return when (val result = ref.oop.invoke(method.functor, method.args)) {
            is Result.Value -> mgu(resultTerm, result.getInvocationResult())
            else -> Substitution.failed()
        }
    }

    protected abstract fun Result.Value.getInvocationResult(): Term
}
