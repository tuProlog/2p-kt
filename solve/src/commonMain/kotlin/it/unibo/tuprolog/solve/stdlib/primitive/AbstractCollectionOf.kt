package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

@Suppress("PrivatePropertyName", "ktlint:standard:property-naming")
abstract class AbstractCollectionOf(
    val name: String,
) : AbstractCollectingPrimitive(name) {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        // the iterated goal of V1^V2^...^G is G, with the variables of every Vi existentially quantified
        val uninteresting = mutableSetOf<Var>()
        var goal = second
        while (goal is Struct && goal.functor == "^" && goal.arity == 2) {
            uninteresting += goal[0].variables
            goal = goal[1]
        }
        val iterated =
            when (goal) {
                is Struct -> goal
                is Var -> throw InstantiationError.forArgument(context, signature, goal, 1)
                else -> throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, goal, 1)
            }
        val solutions = computeIntermediateSolutions(iterated)
        val free = goal.variables.toSet() - first.variables.toSet()
        val interesting = free - uninteresting
        val groups = solutions.groupBy { it.substitution.filter(interesting) }
        val nonPresentable = first.variables.toSet()
        return groups.asSequence().map { (sub, sols) ->
            val solValues =
                sols
                    .map { first[it.substitution] }
                    .filterNot { it in nonPresentable }
                    .map { it.freshCopy() }
            merge(sub, mgu(third, LogicList.of(processSolutions(solValues))))
        }
    }

    protected abstract fun processSolutions(list: List<Term>): Iterable<Term>
}
