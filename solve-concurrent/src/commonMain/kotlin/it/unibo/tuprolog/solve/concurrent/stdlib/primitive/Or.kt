package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.rule.Arrow

object Or : BinaryRelation<ConcurrentExecutionContext>(";") {
    override fun Solve.Request<ConcurrentExecutionContext>.computeAll(
        first: Term,
        second: Term,
    ): Sequence<Solve.Response> {
        ensuringArgumentIsCallable(0)
        ensuringArgumentIsCallable(1)
        if (first is Struct && first.arity == 2 && first.functor == Arrow.functor) {
            val solver: ConcurrentSolver = subSolver() as ConcurrentSolver
            // TODO open an issue to create and ad-hoc ensuringMethod to serve this purpose
            first.args.forEach {
                if (it !is Struct) {
                    throw TypeError.forGoal(context, signature, TypeError.Expected.CALLABLE, it)
                }
            }
            val condition = solver.solveOnce(first[0] as Struct)
            return if (condition.isYes) {
                solver.solve(first[1].apply(condition.substitution).castToStruct())
            } else {
                solver.solve(second as Struct)
            }.map { mapSolution(it, condition.substitution) }
        } else {
            val solver1: ConcurrentSolver = subSolver() as ConcurrentSolver
            val solver2: ConcurrentSolver = subSolver() as ConcurrentSolver
            return (solver1.solve(first.castToStruct()) + solver2.solve(second.castToStruct())).map { mapSolution(it) }
        }
    }

    private fun Solve.Request<ConcurrentExecutionContext>.mapSolution(
        solution: Solution,
        conditionSubstitution: Substitution = Substitution.empty(),
    ): Solve.Response =
        when (solution) {
            is Solution.Yes -> {
                val actualSubstitution = solution.substitution + conditionSubstitution
                if (actualSubstitution is Substitution.Unifier) {
                    replySuccess(actualSubstitution)
                } else {
                    replyFail()
                }
            }
            is Solution.No -> replyFail()
            is Solution.Halt -> replyException(solution.exception.pushContext(context))
        }
}
