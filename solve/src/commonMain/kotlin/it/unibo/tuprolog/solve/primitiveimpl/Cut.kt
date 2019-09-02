package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
object Cut : PrimitiveWrapper(Signature("!", 0)) {

    override val uncheckedImplementation: Primitive = {
        sequenceOf(
                Solve.Response(
                        Solution.Yes(it.signature, it.arguments, it.context.currentSubstitution),
                        it.context.copy(
                                toCutContextsParent = sequence {
                                    yieldAll(it.context.toCutContextsParent)
                                    yieldAll(it.context.clauseScopedParents
                                            .filter(ExecutionContext::isChoicePointChild)
                                            .mapNotNull { ctx -> ctx.clauseScopedParents.firstOrNull() })
                                }
                        )
                )
        )
    }
}