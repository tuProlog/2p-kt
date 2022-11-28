package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

/**
 * Implementation of '=..'/2 predicate
 */
object Univ : BinaryRelation.Functional<ExecutionContext>("=..") {
    private fun Solve.Request<ExecutionContext>.decompose(first: Struct, second: Term): Substitution {
        val decomposed = LogicList.of(Atom.of(first.functor), *first.args.toTypedArray())
        return mgu(second, decomposed)
    }

    private fun Solve.Request<ExecutionContext>.recompose(first: Term, second: LogicList): Substitution {
        val list = second.toList()
        val composed = Struct.of(list[0].castTo<Atom>().value, list.subList(1, list.size))
        return mgu(first, composed)
    }

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when (first) {
            is Struct -> {
                when (second) {
                    is LogicList -> decompose(first, second)
                    is Var -> decompose(first, second)
                    else -> {
                        throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                    }
                }
            }
            is Var -> {
                when (second) {
                    is LogicList -> recompose(first, second)
                    is Var -> {
                        throw InstantiationError.forArgument(context, signature, first, 0)
                    }
                    else -> {
                        throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                    }
                }
            }
            else -> {
                throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, second, 0)
            }
        }
    }
}
