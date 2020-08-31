package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implementation of 'functor'/3 predicate
 */
object Functor : TernaryRelation.Functional<ExecutionContext>("functor") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution = when (first) {
        is Struct -> {
            when (second) {
                is Atom -> {
                    when (third) {
                        is Numeric -> {
                            if (second.value == first.functor && third.intValue.toInt() == first.arity)
                                Substitution.empty()
                            else
                                Substitution.failed()
                        }
                        is Var -> {
                            if (first.functor == second.value)
                                Substitution.of(third to Integer.of(first.arity))
                            else
                                Substitution.failed()
                        }
                        else -> {
                            // TODO expected here should be INTEGER | VARIABLE
                            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, third, 2)
                        }
                    }
                }
                is Var -> {
                    when (third) {
                        is Numeric -> {
                            if (first.arity == third.intValue.toInt())
                                Substitution.of(second to Atom.of(first.functor))
                            else
                                Substitution.failed()
                        }
                        is Var -> {
                            Substitution.of(
                                second to Atom.of(first.functor),
                                third to Integer.of(first.arity)
                            )
                        }
                        else -> {
                            // TODO expected here should be INTEGER | VARIABLE
                            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, third, 2)
                        }
                    }
                }
                else -> {
                    // TODO expected here should be ATOM | VARIABLE
                    throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, second, 1)
                }
            }
        }
        is Var -> {
            when (second) {
                is Atom -> {
                    when (third) {
                        is Numeric -> {
                            Substitution.of(first to Struct.template(second.value, third.intValue.toInt()))
                        }
                        is Var -> {
                            throw InstantiationError.forArgument(context, signature, 2, third)
                        }
                        else -> {
                            // TODO expected here should be INTEGER | VARIABLE
                            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, third, 2)
                        }
                    }
                }
                is Var -> {
                    throw InstantiationError.forArgument(context, signature, 1, second)
                }
                else -> {
                    // TODO expected here should be ATOM | VARIABLE
                    throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, second, 1)
                }
            }
        }
        else -> {
            throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, first, 0)
        }
    }
}