package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object BagOf : TernaryRelation.NonBacktrackable<ExecutionContext>("bagof") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val solutions = solve(second as Struct).toList()
        val error = solutions.asSequence().filterIsInstance<Solution.Halt>().firstOrNull()
        if (error != null) {
            return replyException(error.exception.pushContext(context))
        }
        // list delle istanze che soddisfano P (ovvero lista ottenuta dal findall)
        val mapped = solutions.asSequence()
            .filterIsInstance<Solution.Yes>().map { first[it.substitution] }
        // se contiene ^ deve comportarsi come findall ??
        if (second.toString().contains("^")) {
            return replyWith(third mguWith mapped.toTerm())
        }
        if (mapped.toList().isEmpty()) { // se lista vuota -> fallisce
            return replyFail()
        }
        // tengo conto delle variabili libere che non sono contenute in goal e raggruppo in base a quelle
        val submapped = mapped.asSequence().groupBy { Var }.filterNot { first.isVariable }.count()
        sequence<Substitution> {
            var i = 0
            while (i < submapped) {
                val result = mapped.asSequence().groupBy { Var }.filterNot { first.isVariable }.values.elementAt(i)
                i = i.inc()
                yield(third mguWith result.toTerm())
            }
        }
        return replyWith(third mguWith mapped.toTerm()) // temporanea
        // return replyFail() -> ? chiedere
    }
}
