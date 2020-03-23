package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.AliasedLibrary
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.libraries.stdlib.rule.SpecificRules
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
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