package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

internal object ProbLogLibrary : AliasedLibrary by
    Library.aliased(
        alias = "problog.lang",
        operatorSet = OperatorSet(
            Operator("::", Specifier.XFY, 900),
        )
    ) {
    const val PROB_FUNCTOR = "::"
}