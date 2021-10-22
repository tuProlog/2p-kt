package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.rule.Arrow

object Or : BinaryRelation.WithoutSideEffects<ConcurrentExecutionContext>(";") {
    override fun Solve.Request<ConcurrentExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsCallable(0)
        ensuringArgumentIsCallable(1)
        if (first is Struct && first.arity == 2 && first.functor == Arrow.functor) {
            val solver: ConcurrentSolver = context.createSolver()
            first.args.forEach {
                if (it !is Struct) {
                    throw TypeError.forGoal(context, signature, TypeError.Expected.CALLABLE, it)
                }
            }
            val condition = solver.solveOnce(first[0] as Struct)
            if (condition.isYes) {
                return solver.solve(first[1].apply(condition.substitution).castToStruct()).map {
                    it.substitution
                } // it.substitution + condition.substitution
            } else {
                return solver.solve(second as Struct)
                    .map { it.substitution } // it.substitution + condition.substitution
            }
        } else {
            val solver1: ConcurrentSolver = context.createSolver()
            val solver2: ConcurrentSolver = context.createSolver()
            return solver1.solve(first.castToStruct()).map { it.substitution } +
                solver2.solve(second.castToStruct()).map { it.substitution }
        }
    }
}
