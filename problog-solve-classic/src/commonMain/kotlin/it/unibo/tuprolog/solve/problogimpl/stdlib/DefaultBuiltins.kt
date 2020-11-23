package it.unibo.tuprolog.solve.problogimpl.stdlib

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.problogimpl.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.problogimpl.stdlib.rule.SpecificRules
import it.unibo.tuprolog.theory.Theory

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {
    const val PROB_FUNCTOR = "::"
    const val PROB_NEGATION_FUNC = "\\+::"

    override val alias: String by lazy {
        "problog.lang"
    }

    override val operators: OperatorSet by lazy {
        CommonBuiltins.operators + OperatorSet(
            Operator(PROB_FUNCTOR, Specifier.XFY, 900),
        )
    }

    override val theory: Theory by lazy {
        CommonBuiltins.theory + SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        CommonBuiltins.primitives + sequenceOf(
            Throw
        ).map { it.descriptionPair }.toMap()
    }
}
