package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.gciatto.kt.math.BigInteger

object Arg : TernaryRelation.WithoutSideEffects<ExecutionContext>("arg") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> =
        ensuringArgumentIsInstantiated(1)
            .ensuringArgumentIsCompound(1)
            .run {
                val compound = second as Struct
                return when (first) {
                    is Var -> {
                        compound.argsSequence.mapIndexed { i, arg ->
                            (i + 1) to mgu(arg, third)
                        }.filter { (_, sub) ->
                            sub is Substitution.Unifier
                        }.map { (i, sub) ->
                            sub + Substitution.of(first to Integer.of(i))
                        }
                    }
                    is Integer -> {
                        ensuringArgumentIsNonNegativeInteger(0)
                        if (first.value in BigInteger.ONE..BigInteger.of(compound.arity)) {
                            sequenceOf(mgu(third, compound[first.value.toInt() - 1]))
                        } else {
                            sequenceOf(Substitution.failed())
                        }
                    }
                    else -> {
                        ensuringArgumentIsInteger(0)
                        sequenceOf(Substitution.failed())
                    }
                }
            }
}
