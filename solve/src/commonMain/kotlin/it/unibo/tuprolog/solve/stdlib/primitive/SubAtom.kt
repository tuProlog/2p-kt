package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object SubAtom : QuinaryRelation.WithoutSideEffects<ExecutionContext>("repeat") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term, //string
        second: Term, //before
        third: Term, //length
        fourth: Term,  //after
        fifth: Term // sub
    ): Sequence<Substitution> {
        TODO("Not yet implemented")
    }

}
