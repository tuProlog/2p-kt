package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlin.collections.Set

object SetOf : TernaryRelation.WithoutSideEffects<ExecutionContext>("setof") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        var uninteresting: Set<Var> = emptySet()
        val apexTEMPLATE = Struct.of("^", Var.anonymous(), Var.anonymous())
        if (second matches apexTEMPLATE) {
            if (apexTEMPLATE.getArgAt(1) is Tuple) {
                uninteresting = (apexTEMPLATE.getArgAt(1) as Tuple).toSequence().filterIsInstance<Var>().toSet()
            } else if (apexTEMPLATE.getArgAt(1) is Var) {
                uninteresting = setOf(apexTEMPLATE.getArgAt(1) as Var)
            } else {
                uninteresting = emptySet()
            }
        } else {
            (apexTEMPLATE[1] as Var) mguWith second
        }
        val sols = solve(second as Struct).toList()
        val free = (second.variables - first.variables).toSet()
        val interesting = free.minus(uninteresting)
        val groups = sols.groupBy { it.substitution.filter(interesting) }
        return groups.asSequence().map { (sub, sols) ->
            val listP = sols.filterIsInstance<Solution.Yes>()
                .map { first[it.substitution].freshCopy() }
            sub + Substitution.of(third as Var, listP.toHashSet().toTerm())
        }
    }
}
