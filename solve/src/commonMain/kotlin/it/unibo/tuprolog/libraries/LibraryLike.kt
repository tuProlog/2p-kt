package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

interface LibraryLike {

    val operators: OperatorSet

    val theory: ClauseDatabase

    val primitives: Map<Signature, Primitive>

    operator fun contains(signature: Signature): Boolean =
            primitives.containsKey(signature) || theory.contains(signature.name, signature.arity)

    operator fun contains(operator: Operator): Boolean = operator in operators

    fun isPrimitive(signature: Signature): Boolean = signature in primitives.keys

    fun isProtected(signature: Signature): Boolean = signature in this
}