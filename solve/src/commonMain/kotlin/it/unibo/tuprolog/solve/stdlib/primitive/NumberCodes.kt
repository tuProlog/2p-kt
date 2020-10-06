package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlin.collections.List
import it.unibo.tuprolog.core.List as LogicList

object NumberCodes : BinaryRelation.Functional<ExecutionContext>("number_codes") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when (first) {
            is Var -> {
                ensuringArgumentIsInstantiated(1)
                ensuringArgumentIsList(1)
                val codeList = second as LogicList
                val chars: List<Char> = codeList.toList().map {
                    when (it) {
                        is Integer -> {
                            ensuringTermIsCharCode(it)
                            it.intValue.toChar()
                        }
                        is Var -> {
                            throw InstantiationError.forArgument(context, signature, it, 1)
                        }
                        else -> {
                            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, it, 1)
                        }
                    }
                }
                val number = Atom.of(chars.joinToString(separator = ""))
                Substitution.of(first, (Numeric.of((number.value))))
            }
            else -> {
                ensuringArgumentIsInstantiated(0)
                ensuringArgumentIsNumeric(0)
                if (second !is Var) {
                    ensuringArgumentIsList(1)
                }
                val number = first.toString().toAtom().value
                val result = LogicList.of(number.map { Numeric.of(it.toInt()) })
                second mguWith result
            }
        }
    }
}
