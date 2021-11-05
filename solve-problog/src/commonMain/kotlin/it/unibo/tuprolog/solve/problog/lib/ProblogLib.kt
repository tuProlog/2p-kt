package it.unibo.tuprolog.solve.problog.lib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.problog.PROBLOG_SPECIFIC_OPERATORS
import it.unibo.tuprolog.solve.problog.lib.primitive.SpecificPrimitives
import it.unibo.tuprolog.solve.problog.lib.rules.SpecificRules
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.theory.Theory

object ProblogLib : AliasedLibrary by CommonBuiltins {
    const val EXPLANATION_VAR_NAME = "EXPL"
    const val EVIDENCE_PREDICATE = "evidence"
    const val PREDICATE_PREFIX = "prob"

    override val alias: String
        get() = "problog.lang"

    override val operators: OperatorSet = PROBLOG_SPECIFIC_OPERATORS

    override val theory: Theory by lazy {
        SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        SpecificPrimitives.primitives
    }
}
