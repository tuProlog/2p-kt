package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.error.InstantiationError

/**
 * Implementation of 'functor'/3 predicate
 */
object Functor : NonBacktrackableTernaryRelation<ExecutionContext>("functor") {
    override fun Solve.Request<ExecutionContext>.computeOne(x: Term, y: Term, z: Term): Solve.Response {

        if (x is Struct && y is Atom && z is Integer)
            return replyWith(y.value == x.functor && z.intValue.toInt() == x.arity)
        if (x is Struct && y is Var && z is Var)
            return replySuccess(Substitution.of(
                y to Atom.of(x.functor),
                z to Integer.of(x.arity)) as Substitution.Unifier)
        if (x is Struct && y is Var && z is Integer && z.intValue.toInt() != x.arity)
            return replyFail()
        if (x is Struct && y is Var && z is Integer)
            return replySuccess(Substitution.of(y, Atom.of(x.functor)))
        if (x is Struct && y is Atom && z is Var && y.value != x.functor)
            return replyFail()
        if (x is Struct && y is Atom && z is Var)
            return replySuccess(Substitution.of(z, Integer.of(x.arity)))
        if (x is Var && y is Atom && z is Integer)
            return replySuccess(Substitution.of(x, createFunctor(y.value, z.intValue.toInt())))

        return replyException(InstantiationError.forArgument(context, signature))
    }

    private fun createFunctor(name: String, arity: Int) : Struct =
        Struct.of(name, (1..arity).map { Var.of("X") })
}