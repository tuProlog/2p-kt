package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object AtomChars : BinaryRelation.Functional<ExecutionContext>("atom_chars") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
        when {
            first is Var && second is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }
            first is Var -> {
                ensuringArgumentIsList(1)
                val chars = second as LogicList
                val atom: Atom = Atom.of(
                    chars.toSequence().map {
                        if (it is Atom) {
                            it.value[0]
                        } else {
                            throw TypeError.forArgumentList(context, signature, TypeError.Expected.CHARACTER, it, 1)
                        }
                    }.joinToString("")
                )
                Substitution.of(first, atom)
            }
            second is Var -> {
                ensuringArgumentIsAtom(0)
                val charArray = (first as Atom).value
                val chars = LogicList.of(charArray.map { Atom.of("" + it) })
                Substitution.of(second, chars)
            }
            else -> {
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsList(1)
                val chars = LogicList.of(
                    (first as Atom).value.map { Atom.of("" + it) }
                )
                mgu(chars, second)
            }
        }
}
