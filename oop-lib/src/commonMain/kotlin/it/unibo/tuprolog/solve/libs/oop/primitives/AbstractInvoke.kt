package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * The shared implementation of `invoke_$suffix/3` (first arg: a [Ref] or `$Alias` expression;
 * second: the `Method(Arg1, ..., ArgN)` [Struct] to invoke; third: unified with whatever
 * [getInvocationResult] extracts from the invocation's [Result]), backing both `invoke_method/3`
 * ([InvokeMethod]) and `invoke_strict/3` ([InvokeStrict]) -- the two only differ in whether the
 * returned value is converted to a plain [Term] or kept as an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef].
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the first argument is not a
 * [Struct], or the second is not a [Struct].
 */
abstract class AbstractInvoke(
    suffix: String,
) : TernaryRelation.Functional<ExecutionContext>("invoke_$suffix") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
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

    private fun Solve.Request<ExecutionContext>.actuallyInvoke(
        ref: Ref,
        method: Struct,
        resultTerm: Term,
    ): Substitution =
        when (val result = ref.invoke(termToObjectConverter, method.functor, method.args)) {
            is Result.Value -> mgu(resultTerm, result.getInvocationResult())
            else -> Substitution.failed()
        }

    /** Extracts the [Term] to unify the third argument with, from a successful invocation's [Result.Value]. */
    protected abstract fun Result.Value.getInvocationResult(): Term
}
