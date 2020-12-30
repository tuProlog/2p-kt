package it.unibo.tuprolog.solve.problog.lib

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.problog.lib.primitive.SpecificPrimitives
import it.unibo.tuprolog.solve.problog.lib.rule.SpecificRules
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.theory.Theory

object ProblogLib : AliasedLibrary by CommonBuiltins {
    const val DD_VAR_NAME = "_DD"
    const val EVIDENCE_PREDICATE = "evidence"
    const val PREDICATE_PREFIX = "_prob"
    const val PROB_FUNCTOR = "::"

    override val alias: String
        get() = "problog.lang"

    override val operators: OperatorSet by lazy {
        OperatorSet(
            Operator(PROB_FUNCTOR, Specifier.XFY, 900),
        )
    }

    override val theory: Theory by lazy {
        SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        SpecificPrimitives.primitives
    }
}
