package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.core.List as LogicList

object NumberChars : BinaryRelation.Functional<ExecutionContext>("number_chars") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when {
            first is Var && second is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }
            first is Var -> {
                ensuringArgumentIsList(1)
                val chars = second as LogicList
                val number: Atom = Atom.of(
                    chars.toSequence().map { (it as Atom).value[0] }.joinToString("").trim()
                )
                Substitution.of(first, (Numeric.of((number.value))))
            }
            second is Var -> {
                ensuringArgumentIsNumeric(0)
                val number = (first).toString().toAtom().value
                val chars = LogicList.of(number.map { Atom.of("" + it) })
                Substitution.of(second, chars)
            }
            else -> {
                ensuringArgumentIsNumeric(0)
                ensuringArgumentIsList(1)
                val chars = LogicList.of(
                    (first).toString().toAtom().value.map { Atom.of("" + it) }
                )
                chars mguWith second
            }
        }
    }
}
