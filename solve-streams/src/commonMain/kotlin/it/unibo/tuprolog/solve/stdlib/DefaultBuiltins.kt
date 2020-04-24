package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.primitive.*

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