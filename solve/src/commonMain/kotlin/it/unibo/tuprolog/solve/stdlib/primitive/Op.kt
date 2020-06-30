package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SideEffect
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object Op : TernaryRelation.NonBacktrackable<ExecutionContext>("op") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInteger(0)
        ensuringArgumentIsNonNegativeInteger(0)
        ensuringArgumentIsAtom(1)
        ensuringArgumentIsSpecifier(1)
        ensuringArgumentIsAtom(2)
        val operator = Operator.fromTerms(first as Integer, second as Atom, third as Atom)!!
        return replySuccess(
            Substitution.empty(),
            null,
            SideEffect.SetOperators(operator)
        )
    }
}