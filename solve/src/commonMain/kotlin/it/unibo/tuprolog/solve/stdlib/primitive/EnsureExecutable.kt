package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TypeEnsurer

object EnsureExecutable : TypeEnsurer<ExecutionContext>("ensure_executable") {
    override fun Solve.Request<ExecutionContext>.ensureType(context: ExecutionContext, term: Term) {
        val signature = context.procedure?.extractSignature() ?: signature
        when (term) {
            is Var -> {
                val variable = context.substitution.getOriginal(term) ?: term
                throw InstantiationError.forGoal(context, signature, variable)
            }
            else -> {
                checkTermIsRecursivelyCallable(term)?.let {
                    throw TypeError.forGoal(context, signature, it.expectedType, term)
                }
            }
        }
    }
}
