package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
object Cut : PrimitiveWrapper(Signature("!", 0)) {

    override val uncheckedImplementation: Primitive = {
        // TODO: 25/09/2019 remove that
        it as Solve.Request<ExecutionContextImpl>

        sequenceOf(
                Solve.Response(
                        Solution.Yes(it.signature, it.arguments, it.context.substitution),
                        context = it.context.copy(
                                toCutContextsParent = sequence {
                                    yieldAll(it.context.toCutContextsParent)
                                    yieldAll(it.context.clauseScopedParents
                                            .filter(ExecutionContextImpl::isChoicePointChild)
                                            .mapNotNull { ctx -> ctx.clauseScopedParents.firstOrNull() })
                                }
                        )
                )
        )
    }
}