package it.unibo.tuprolog.solve.libraries.stdlib

import it.unibo.tuprolog.solve.libraries.AliasedLibrary
import it.unibo.tuprolog.solve.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.libraries.stdlib.rule.SpecificRules
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Signature
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