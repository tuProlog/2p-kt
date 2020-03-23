package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.AliasedLibrary
import it.unibo.tuprolog.libraries.stdlib.primitive.*
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Call,
            Catch,
            Conjunction,
            Cut,
            Throw,
            Not
        ).map { it.descriptionPair }.toMap()
    }
}