package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * atom_concat(ab, cd, abcd).
 */

object AtomConcat : TernaryRelation.Functional<ExecutionContext>("atom_concat") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution =
        when {
            third is Var -> {
                ensuringArgumentIsInstantiated(0)
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsInstantiated(1)
                ensuringArgumentIsAtom(1)
                val result = (first as Atom).value + (second as Atom).value
                Substitution.of(third, Atom.of(result))
            }

            first is Var -> {
                ensuringArgumentIsInstantiated(1)
                ensuringArgumentIsAtom(1)
                ensuringArgumentIsInstantiated(2)
                ensuringArgumentIsAtom(2)
                val secondValue = (second as Atom).value
                val thirdValue = (third as Atom).value
                if (thirdValue.endsWith(secondValue)) {
                    Substitution.of(first, Atom.of(thirdValue.removeSuffix(secondValue)))
                } else {
                    Substitution.failed()
                }
            }

            second is Var -> {
                ensuringArgumentIsInstantiated(0)
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsInstantiated(2)
                ensuringArgumentIsAtom(2)
                val firstValue = (first as Atom).value
                val thirdValue = (third as Atom).value
                if (thirdValue.startsWith(firstValue)) {
                    Substitution.of(second, Atom.of(thirdValue.removePrefix(firstValue)))
                } else {
                    Substitution.failed()
                }
            }

            else -> {
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsAtom(1)
                ensuringArgumentIsAtom(2)
                val firstString = (first as Atom).value
                val secondString = (second as Atom).value
                val thirdString = firstString + secondString
                mgu(third, Atom.of(thirdString))
            }
        }
}
