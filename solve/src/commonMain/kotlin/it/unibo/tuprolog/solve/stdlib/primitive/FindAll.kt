package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.error.MetaError
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.core.List as LogicList

object FindAll : TernaryRelation.NonBacktrackable<ExecutionContext>("findall") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        val solutions = solve(second as Struct).toList()
        val error = solutions.asSequence().filterIsInstance<Solution.Halt>().firstOrNull()
        if (error != null) {
            return replyException(MetaError.of(context, error.exception))
        }
        val mapped = solutions.asSequence()
            .filterIsInstance<Solution.Yes>()
            .map { first[it.substitution].freshCopy() }

        return replySuccess(third.mguWith(LogicList.from(mapped)) as Substitution.Unifier)
    }
}