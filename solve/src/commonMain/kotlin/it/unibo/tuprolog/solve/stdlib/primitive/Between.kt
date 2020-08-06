package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.Integer as LogicInteger

object Between : TernaryRelation.WithoutSideEffects<ExecutionContext>("between") {

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsInteger(0)
        ensuringArgumentIsInteger(1)

        val bottom = (first as LogicInteger).value
        val top = (second as LogicInteger).value

        return when (third) {
            is LogicInteger -> {
                val x = third.value
                sequenceOf(if (x in bottom .. top) Substitution.empty() else Substitution.failed())
            }
            is Var -> {
                if (bottom <= top) {
                    generateSequence(bottom) {
                        it + BigInteger.ONE
                    }.takeWhile {
                        it <= top
                    }.map {
                        LogicInteger.of(it)
                    }.map {
                        Substitution.of(third, it)
                    }
                } else {
                    sequenceOf(Substitution.failed())
                }
            }
            else -> {
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, third, 2)
            }
        }
    }
}