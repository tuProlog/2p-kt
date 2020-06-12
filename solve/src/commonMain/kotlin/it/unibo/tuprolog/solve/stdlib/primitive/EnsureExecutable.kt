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
    private fun ensurerVisitor(context: ExecutionContext, procedure: Signature): TermVisitor<Unit> =
        object : TermVisitor<Unit> {
            override fun defaultValue(term: Term) { /* does nothing */ }

            override fun visit(term: Struct) = when {
                term.functor in Clause.notableFunctors && term.arity == 2 ->
                    term.argsSequence.forEach { arg -> arg.accept(this) }
                else -> defaultValue(term)
            }

            override fun visit(term: Numeric) {
                throw TypeError.forGoal(context, procedure, TypeError.Expected.CALLABLE, term)
            }
        }

    override fun ensureType(context: ExecutionContext, term: Term) {
        when (term) {
            is Var -> {
                throw InstantiationError.forGoal(
                    context,
                    context.procedure!!.extractSignature(),
                    context.substitution.getOriginal(term) ?: term
                )
            }
            else -> {
                term.accept(ensurerVisitor(context, context.procedure!!.extractSignature()))
            }
        }
    }

}