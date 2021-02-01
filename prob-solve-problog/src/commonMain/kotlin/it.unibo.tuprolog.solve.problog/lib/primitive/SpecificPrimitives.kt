package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

internal object SpecificPrimitives {
    private val wrappers: Sequence<PrimitiveWrapper<*>> =
        sequenceOf(
            Prob,
            ProbSolve,
            ProbExplNot,
            ProbExplAnd,
            ProbCalc,
            ProbQuery,
            ProbSolveEvidence,
            ProbSolveWithEvidence,
            ProbNegationAsFailure,
            ProbExplDebug,
            ProbSetMode,
        )

    val primitives: Map<Signature, Primitive> = wrappers.map { it.descriptionPair }.toMap()
}
