package it.unibo.tuprolog.solve.classic.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.classic.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.classic.stdlib.rule.SpecificRules
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.theory.Theory

object DefaultBuiltins : Library by CommonBuiltins {

    override val theory: Theory by lazy {
        CommonBuiltins.theory + SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Throw
        ).map { it.descriptionPair }.toMap()
    }
}
