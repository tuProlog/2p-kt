package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object AtomCodes : BinaryRelation.Functional<ExecutionContext>("atom_codes") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when {
            first is Var && second is Var -> {
                ensuringArgumentIsInstantiated(0)
                Substitution.failed()
            }
            first is Var -> {
                ensuringArgumentIsList(1)
                val codeList = second as LogicList
                var i = 0
                var result = ""
                while (i < codeList.toSequence().count()) {
                    result += codeList[i].toString().toInt().toChar().toString()
                    i++
                }
                Substitution.of(first, Atom.of(result))
            }
            second is Var -> {
                ensuringArgumentIsAtom(0)
                val charArray = (first as Atom).value
                //val result = listOf(charArray.forEach{(it.toByte().toInt())})
                val result = LogicList.of(charArray.map { Atom.of("" + it.toByte().toInt().toString()) })
                Substitution.of(second, result)
            }
            else -> {
                Substitution.failed()
            }
        }
    }
}