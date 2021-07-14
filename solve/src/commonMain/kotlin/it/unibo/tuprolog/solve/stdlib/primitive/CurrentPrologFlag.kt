package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object CurrentPrologFlag : BinaryRelation.WithoutSideEffects<ExecutionContext>("current_prolog_flag") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> = when (first) {
        is Atom, is Var -> {
            context.flags.asSequence()
                .map { (k, v) -> Atom.of(k) to v }
                .map { (k, v) -> (k mguWith first) + (v mguWith second) }
                .filter { it.isSuccess }
        }
        else -> {
            ensuringArgumentIsAtom(0)
            emptySequence()
        }
    }
}
