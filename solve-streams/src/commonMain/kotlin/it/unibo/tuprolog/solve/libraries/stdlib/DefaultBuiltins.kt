package it.unibo.tuprolog.solve.libraries.stdlib

import it.unibo.tuprolog.solve.libraries.AliasedLibrary
import it.unibo.tuprolog.solve.libraries.stdlib.primitive.*
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Signature

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