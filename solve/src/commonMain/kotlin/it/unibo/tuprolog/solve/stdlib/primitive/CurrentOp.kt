package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object CurrentOp : TernaryRelation.WithoutSideEffects<ExecutionContext>("current_op") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> =
        context.operators.asSequence().map {
            listOf(
                mgu(first, Integer.of(it.priority)),
                mgu(second, it.specifier.toTerm()),
                mgu(third, Atom.of(it.functor)),
            )
        }.filter {
            it.all { sub -> sub is Substitution.Unifier }
        }.map { it.reduce(Substitution::plus) }
}
