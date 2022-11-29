package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
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
                val numberString = chars.joinToString(separator = "")
                Substitution.of(first, Numeric.of(numberString))
            }
            else -> {
                ensuringArgumentIsInstantiated(0)
                ensuringArgumentIsNumeric(0)
                if (second !is Var) {
                    ensuringArgumentIsList(1)
                }
                val numberString = first.toString()
                val result = LogicList.of(numberString.map { Integer.of(it.code) })
                mgu(second, result)
            }
        }
    }
}
