package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.primitive.Call
import it.unibo.tuprolog.solve.stdlib.primitive.Catch
import it.unibo.tuprolog.solve.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.stdlib.primitive.Not
import it.unibo.tuprolog.solve.stdlib.primitive.Throw

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Call,
            Catch,
            Conjunction,
            Cut,
            Throw,
            Not,
            FindAll
        ).map { it.descriptionPair }.toMap()
    }
}
