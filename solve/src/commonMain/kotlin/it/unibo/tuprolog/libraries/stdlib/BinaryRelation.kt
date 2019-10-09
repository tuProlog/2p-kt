package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** Base class to implement primitives that relate two [Term]s and provide a single response */
abstract class BinaryRelation<E : ExecutionContext>(operator: String) : PrimitiveWrapper<E>(operator, 2) {

    /** Template method that should compute the response of the [Term] relation application  */
    protected abstract fun Solve.Request<E>.computeSingleResponse(): Solve.Response

    /** Template method that should be implemented and called in BinaryRelations without side effects */
    protected open fun relationWithoutSideEffects(x: Term, y: Term): Boolean = throw NotImplementedError()

    /** Template method that should be implemented and called in BinaryRelations with side effects */
    protected open fun relationWithSideEffects(x: Term, y: Term): Substitution = throw NotImplementedError()
}
