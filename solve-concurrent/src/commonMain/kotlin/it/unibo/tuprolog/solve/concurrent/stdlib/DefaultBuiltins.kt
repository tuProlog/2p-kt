package it.unibo.tuprolog.solve.concurrent.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.concurrent.stdlib.rule.SpecificRules
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.theory.Theory

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {

    override val theory: Theory by lazy {
        CommonBuiltins.theory + SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives /* + sequenceOf( // todo uncomment when add primitive
            Throw
        ).map { it.descriptionPair }.toMap()*/
    }
}