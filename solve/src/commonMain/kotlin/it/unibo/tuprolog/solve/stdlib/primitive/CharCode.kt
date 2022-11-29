package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object CharCode : BinaryRelation.Functional<ExecutionContext>("char_code") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when {
            first is Var -> {
                ensuringArgumentIsInstantiated(1)
                ensuringArgumentIsCharCode(1)
                val atom: Atom = Atom.of(
                    charArrayOf((second as Integer).intValue.toChar()).concatToString()
                )
                Substitution.of(first, atom)
            }
            else -> {
                ensuringArgumentIsInstantiated(0)
                ensuringArgumentIsChar(0)
                val result = (first as Atom).value[0].code
                mgu(second, Integer.of(result))
            }
        }
    }
}
