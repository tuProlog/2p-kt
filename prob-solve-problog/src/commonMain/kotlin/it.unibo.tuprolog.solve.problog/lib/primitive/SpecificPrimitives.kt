package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

internal object SpecificPrimitives {
    private val wrappers: Sequence<PrimitiveWrapper<*>> =
        sequenceOf(
            ProbEnsurePrologCall,
            ProbExplNot,
            ProbExplAnd,
            ProbSolve,
            ProbCalc,
            ProbQuery,
            ProbSolveEvidence,
            ProbSolveWithEvidence,
            ProbNegationAsFailure,
            ProbExplDebug
        )

    val primitives: Map<Signature, Primitive> = wrappers.map { it.descriptionPair }.toMap()
}
