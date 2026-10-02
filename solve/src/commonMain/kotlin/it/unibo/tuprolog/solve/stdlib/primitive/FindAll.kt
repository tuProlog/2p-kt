package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object FindAll : AbstractCollectingPrimitive("findall") {
    override val help: String =
        """
        `findall(?Template, +Goal, -Bag)`
        
        Collects every instance of `Template` produced by solutions of `Goal` into `Bag`, preserving duplicates and solution order. Unlike `bagof/3`, free variables of `Goal` do not create separate groups; failure yields an empty list.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val solutions = computeIntermediateSolutions(second.castToStruct())
        val mapped = solutions.map { first[it.substitution].freshCopy() }
        return sequenceOf(mgu(third, LogicList.of(mapped)))
    }
}
