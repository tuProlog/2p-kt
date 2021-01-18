package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

internal object SpecificPrimitives {
    private val wrappers: Sequence<PrimitiveWrapper<*>> =
        sequenceOf(
            ProbExplNot,
            ProbExplAnd,
            ProbSolve,
            ProbCalc,
            ProbSolveEvidence,
            EnsurePrologCall,
            ProbDebugSolution
        )

    val primitives: Map<Signature, Primitive> = wrappers.map { it.descriptionPair }.toMap()
}
