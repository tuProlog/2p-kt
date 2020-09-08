package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.TypeEnsurer

object EnsureExecutable : TypeEnsurer<ExecutionContext>("ensure_executable") {
    private fun ensurerVisitor(context: ExecutionContext, procedure: Signature): TermVisitor<TypeError?> =
        object : TermVisitor<TypeError?> {
            override fun defaultValue(term: Term): Nothing? = null

            override fun visit(term: Struct) = when {
                term.functor in Clause.notableFunctors && term.arity == 2 -> {
                    term.argsSequence.map { it.accept(this) }.filterNotNull().firstOrNull()
                }
                else -> defaultValue(term)
            }

            override fun visit(term: Numeric): TypeError {
                return TypeError.forGoal(context, procedure, TypeError.Expected.CALLABLE, term)
            }
        }

    override fun ensureType(context: ExecutionContext, term: Term) {
        val signature = context.procedure?.extractSignature() ?: signature
        when (term) {
            is Var -> {
                val variable = context.substitution.getOriginal(term) ?: term
                throw InstantiationError.forGoal(context, signature, variable)
            }
            else -> {
                term.accept(ensurerVisitor(context, signature))?.let {
                    throw TypeError.forGoal(context, signature, it.expectedType, term)
                }
            }
        }
    }

}