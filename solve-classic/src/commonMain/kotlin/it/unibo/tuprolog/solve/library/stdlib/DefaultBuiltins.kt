package it.unibo.tuprolog.solve.library.stdlib

import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.library.stdlib.rule.SpecificRules
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {

    override val theory: ClauseDatabase by lazy {
        CommonBuiltins.theory + SpecificRules.clauseDb
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Throw
        ).map { it.descriptionPair }.toMap()
    }
}