package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation


object AtomConcat : TernaryRelation.Functional<ExecutionContext>("atom_concat") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        return when {
            first is Var && third is Var || second is Var && third is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }

            third is Var -> {
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsAtom(1)
                val result = (first as Atom).toString() + (second as Atom).toString()
                Substitution.of(third, Atom.of(result))
            }

            first is Var -> {
                ensuringArgumentIsAtom(1)
                ensuringArgumentIsAtom(2)
                val result = third.toString().substringBefore((second as Atom).toString())
                Substitution.of(first, Atom.of(result))

            }

            second is Var -> {
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsAtom(2)
                val result = third.toString().substringAfter((first as Atom).toString())
                Substitution.of(second, Atom.of(result))
            }

            else -> {
                Substitution.failed()
            }
        }
    }

}

