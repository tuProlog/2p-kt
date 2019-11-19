package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

object DefaultBuiltins : LibraryAliased by CommonBuiltins {

    override val theory: ClauseDatabase by lazy {
        CommonBuiltins.theory + ClauseDatabase.of(
            { ruleOf(tupleOf(varOf("A"), varOf("B")), varOf("A"), varOf("B")) },
            { ruleOf(structOf("call", varOf("X")), varOf("X")) },
            { ruleOf(structOf("catch", varOf("G"), whatever(), whatever()), varOf("G")) }
        )
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Throw
        ).map { it.descriptionPair }.toMap()
    }
}