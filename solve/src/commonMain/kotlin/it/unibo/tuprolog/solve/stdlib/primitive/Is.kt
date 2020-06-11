package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.function.ArithmeticEvaluator
import it.unibo.tuprolog.solve.primitive.TermRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * Implementation of 'is'/2 predicate
 *
 * @author Enrico
 */
object Is : TermRelation.WithSideEffects<ExecutionContext>("is") {

    override fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response =
        ArithmeticEvaluator(context).let {
            when (val effects: Substitution = relationWithSideEffects(arguments[0], arguments[1].accept(it))) {
                is Substitution.Unifier -> replySuccess(effects)
                else -> replyFail()
            }
        }

    override fun relationWithSideEffects(x: Term, y: Term): Substitution =
        x mguWith y
}
