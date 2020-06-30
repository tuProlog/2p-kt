package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.core.List as LogicList
import kotlin.collections.listOf as ktListOf

/**
 * Implementation of '=..'/2 predicate
 */
object Univ : BinaryRelation.Functional<ExecutionContext>("=..") {
    private fun univ(first: Term, second: it.unibo.tuprolog.core.List): Substitution {
        val list = second.toList()
        return if (list.isNotEmpty() && list[0] is Atom) {
            first mguWith Struct.of(list[0].castTo<Atom>().value, list.drop(1))
        } else {
            Substitution.failed()
        }
    }

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when (first) {
            is Struct -> {
                when (second) {
                    is LogicList -> {
                        univ(first, second)
                    }
                    is Var -> {
                        second mguWith LogicList.of(
                            ktListOf(Atom.of(first.functor)) + first.argsList
                        )
                    }
                    else -> {
                        // TODO expected here should be LIST | VARIABLE or something like that
                        throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                    }
                }
            }
            is Var -> {
                when (second) {
                    is LogicList -> {
                        univ(first, second)
                    }
                    is Var -> {
                        throw InstantiationError.forArgument(context, signature, 0, first)
                    }
                    else -> {
                        // TODO expected here should be LIST | VARIABLE or something like that
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
