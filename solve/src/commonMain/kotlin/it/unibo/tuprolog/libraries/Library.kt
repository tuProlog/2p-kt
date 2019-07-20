package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.theory.ClauseDatabase

interface Library {

    val operators: OperatorSet

    val theory: ClauseDatabase

    val primitives: Map<Signature, Primitive>

    val alias: String

    operator fun contains(signature: Signature): Boolean =
            primitives.containsKey(signature)

    fun isPrimitive(signature: Signature): Boolean = signature in this

}