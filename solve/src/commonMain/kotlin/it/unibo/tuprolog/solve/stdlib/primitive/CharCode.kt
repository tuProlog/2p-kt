package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object CharCode : BinaryRelation.Functional<ExecutionContext>("char_code") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when {
            first is Var && second is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }
            first is Var -> {
                ensuringArgumentIsCharCode(1)
                val atom: Atom = Atom.of(
                    second.toString().toInt().toChar().toString()
                )
                Substitution.of(first, atom)
            }
            second is Var -> {
                val result = first.toString().first().toByte().toInt()
                Substitution.of(second, result.toTerm())
            }
            else -> {
                ensuringArgumentIsCharCode(1)
                val result = first.toString().first().toByte().toInt()
                result.toTerm() mguWith second
            }

        }
    }

}