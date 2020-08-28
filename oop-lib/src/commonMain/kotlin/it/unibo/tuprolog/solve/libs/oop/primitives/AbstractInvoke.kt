package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

abstract class AbstractInvoke(suffix: String) : TernaryRelation.Functional<ExecutionContext>("invoke_$suffix") {

    private fun Solve.Request<ExecutionContext>.findRefFromAlias(alias: Struct): Ref? {
        val ActualRef = Var.of("ActualRef")
        val found = solve(Struct.of(Alias.FUNCTOR, alias, ActualRef)).toList()
        if (found.isNotEmpty()) {
            return found.filterIsInstance<Solution.Yes>().firstOrNull()?.solvedQuery?.get(1)?.castTo()
        }
        return null
    }

    private fun actuallyInvoke(ref: Ref, method: Struct, resultTerm: Term): Substitution {
        return when (val result = ref.invoke(method.functor, *method.args)) {
            is Result.Value -> resultTerm mguWith result.getInvocationResult()
            else -> Substitution.failed()
        }
    }

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsStruct(1)

        val method = second as Struct

        return when (first) {
            is Ref -> {
                actuallyInvoke(first, method, third)
            }
            else -> {
                when (val ref = findRefFromAlias(first as Struct)) {
                    null -> Substitution.failed()
                    else -> actuallyInvoke(ref, method, third)
                }
            }
        }
    }

    protected abstract fun Result.Value.getInvocationResult(): Term

}