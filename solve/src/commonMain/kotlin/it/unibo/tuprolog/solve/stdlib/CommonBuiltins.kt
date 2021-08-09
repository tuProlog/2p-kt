package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object CommonBuiltins : AliasedLibrary by
Library.aliased(
    alias = "prolog.lang",
    primitives = CommonPrimitives.primitives,
    theory = CommonRules.theory,
    operatorSet = OperatorSet.DEFAULT,
    functions = CommonFunctions.functions
)
