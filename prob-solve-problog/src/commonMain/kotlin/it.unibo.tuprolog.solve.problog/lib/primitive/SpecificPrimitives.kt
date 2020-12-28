package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper

object SpecificPrimitives {
    private val wrappers: Sequence<PrimitiveWrapper<*>> =
        sequenceOf(
            ProbBuildNot,
            ProbBuildAnd,
            ProbSolve,
            ProbSolveEvidence,
            ProbCalc,
            EnsureBuiltin
        )

    val primitives: Map<Signature, Primitive> = wrappers.map { it.descriptionPair }.toMap()
}
