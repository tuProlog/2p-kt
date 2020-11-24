package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlin.collections.Set
import it.unibo.tuprolog.core.List as LogicList

abstract class AbstractSetOfAndBagOf(val name: String, val set: Boolean) : TernaryRelation.WithoutSideEffects<ExecutionContext>(name) {
    private val VARS = Var.of("VARS")
    private val GOAL = Var.of("GOAL")

    private val APEX_TEMPLATE = Struct.of("^", VARS, GOAL)

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val mgu = APEX_TEMPLATE mguWith second
        val uninteresting: Set<Var> = when (mgu) {
            is Substitution.Unifier -> when (val vars = mgu[VARS]) {
                is Tuple -> vars.toSequence().filterIsInstance<Var>().toSet()
                is Var -> setOf(vars)
                else -> emptySet()
            }
            else -> emptySet()
        }
        val goal = if (mgu is Substitution.Unifier) mgu[GOAL] else second
        val solutions = solve(goal as Struct)
            .map {
                when (it) {
                    is Solution.Halt -> throw it.exception.pushContext(context)
                    else -> it
                }
            }.filterIsInstance<Solution.Yes>().toList()
        val free = goal.variables.toSet() - first.variables.toSet()
        val interesting = free - uninteresting
        val groups = solutions.groupBy { it.substitution.filter(interesting) }
        val nonPresentable = first.variables.toSet()
        return groups.asSequence().map { (sub, sols) ->
            val solValues =
                sols.map { first[it.substitution] }.filterNot { it in nonPresentable }.map { it.freshCopy() }
            if (!set) {
                sub + (third mguWith LogicList.of(solValues))
            } else {
                sub + (third mguWith LogicList.of(solValues.toHashSet()))
            }
        }
    }
}
