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
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import org.gciatto.kt.math.BigInteger

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
            if (third !is Var) {
                ensuringArgumentIsNonNegativeInteger(2)
            }
            (second mguWith Atom.of(first.functor)) + (third mguWith Integer.of(first.arity))
        }
        is Numeric -> {
            if (third !is Var) {
                ensuringArgumentIsNonNegativeInteger(2)
            }
            (first mguWith second) + (third mguWith Integer.of(0))
        }
        is Var -> {
            when (second) {
                is Atom -> {
                    ensuringArgumentIsInstantiated(2)
                    ensuringArgumentIsNonNegativeInteger(2)
                    Substitution.of(first to Struct.template(second.value, (third as Integer).intValue.toInt()))
                }
                is Numeric -> {
                    ensuringArgumentIsInstantiated(2)
                    ensuringArgumentIsNonNegativeInteger(2)

                    if ((third as Integer).intValue == BigInteger.ZERO) {
                        Substitution.of(first to second) + (third mguWith Integer.of(0))
                    } else {
                        throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, second, 1)
                    }
                }
                is Var -> {
                    throw InstantiationError.forArgument(context, signature, 1, second)
                }
                else -> {
                    throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, second, 1)
                }
            }
        }
        else -> {
            throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, first, 0)
        }
    }
}